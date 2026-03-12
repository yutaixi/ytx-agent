package com.ytx.ai.agent.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ytx.ai.agent.dto.FileUploadResult;
import com.ytx.ai.agent.entity.FilesEntity;
import com.ytx.ai.agent.entity.FilesInsEntity;
import com.ytx.ai.agent.mapper.FilesInsMapper;
import com.ytx.ai.agent.mapper.FilesMapper;
import com.ytx.ai.agent.service.FileService;
import com.ytx.ai.oss.config.OssProperties;
import com.ytx.ai.oss.service.OssClient;
import com.ytx.ai.oss.util.OssPathUtil;
import com.ytx.ai.oss.vo.FileMeta;
import com.ytx.ai.oss.vo.OssUploadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final FilesMapper filesMapper;
    private final FilesInsMapper filesInsMapper;
    private final OssClient ossClient;
    private final OssProperties ossProperties;
    private final Tika tika = new Tika();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FilesInsEntity addFile(FilesEntity filesEntity, String name, String url) {
        // Check if MD5 exists
        LambdaQueryWrapper<FilesEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FilesEntity::getMd5, filesEntity.getMd5());
        FilesEntity existingFile = filesMapper.selectOne(queryWrapper);

        Integer fileId;
        if (existingFile != null) {
            fileId = existingFile.getId();
        } else {
            filesMapper.insert(filesEntity);
            fileId = filesEntity.getId();
        }

        return saveAiFilesIns(name, url, fileId);
    }

    @Override
    public boolean deleteFile(Integer id) {
        return filesInsMapper.deleteById(id) > 0;
    }

    @Override
    public FilesInsEntity getFileInstance(Integer id) {
        return filesInsMapper.selectById(id);
    }

    @Override
    public List<FilesInsEntity> listFileInstances() {
        return filesInsMapper.selectList(null);
    }

    @Override
    public FilesEntity getFileByMd5(String md5) {
        LambdaQueryWrapper<FilesEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FilesEntity::getMd5, md5);
        return filesMapper.selectOne(queryWrapper);
    }

    /**
     * 上传文件
     * 1. 计算文件MD5
     * 2. 检查文件是否已存在
     * 3. 如果存在，复用现有文件信息
     * 4. 如果不存在，上传到OSS并保存文件信息
     * 5. 保存文件实例信息
     *
     * @param file 上传的文件
     * @return 文件上传结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadResult uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String md5 = calculateMd5(file);

        // Check if file exists by MD5
        FilesEntity existingFile = getFileByMd5(md5);

        if (existingFile != null) {
            return handleExistingFile(existingFile, originalFilename);
        } else {
            return handleNewFile(file, originalFilename, md5);
        }
    }

    /**
     * 计算文件MD5
     *
     * @param file 上传的文件
     * @return MD5字符串
     */
    private String calculateMd5(MultipartFile file) {
        try {
            return DigestUtils.md5DigestAsHex(file.getInputStream());
        } catch (IOException e) {
            log.error("Failed to calculate MD5 for file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to calculate MD5", e);
        }
    }

    /**
     * 处理已存在的文件
     *
     * @param existingFile 已存在的文件信息
     * @param originalFilename 原始文件名
     * @return 文件上传结果
     */
    private FileUploadResult handleExistingFile(FilesEntity existingFile, String originalFilename) {
        log.info("File with MD5 {} already exists. Reusing existing file.", existingFile.getMd5());

        // Generate signed URL
        String url = ossClient.getUrl(existingFile.getPath(), 3600L, originalFilename);

        // Save file instance
        FilesInsEntity filesInsEntity = saveAiFilesIns(originalFilename, url, existingFile.getId());

        FileMeta meta = FileMeta.builder()
                .name(originalFilename)
                .path(existingFile.getPath())
                .suffix(existingFile.getSuffix())
                .mimeType(existingFile.getMimeType())
                .size(existingFile.getSize() != null ? existingFile.getSize().longValue() : 0L)
                .storeType(existingFile.getStoreType())
                .build();

        return new FileUploadResult(
                String.valueOf(filesInsEntity.getId()),
                url,
                meta);
    }

    /**
     * 处理新文件上传
     *
     * @param file 上传的文件
     * @param originalFilename 原始文件名
     * @param md5 文件MD5
     * @return 文件上传结果
     */
    private FileUploadResult handleNewFile(MultipartFile file, String originalFilename, String md5) {
        long size = file.getSize();
        log.info("Uploading new file: {}", originalFilename);
        String newPath = OssPathUtil.generateDateUuidOriginalPath(originalFilename);

        // Detect mime type
        String mimeType = detectMimeType(file, originalFilename);

        // Upload to OSS
        String url = uploadToOss(file, newPath, originalFilename);

        // Save FilesEntity
        FilesEntity filesEntity = saveAiFiles(newPath, originalFilename, mimeType, size, md5);

        // Save FilesInsEntity
        FilesInsEntity filesInsEntity = saveAiFilesIns(originalFilename, url, filesEntity.getId());

        FileMeta meta = FileMeta.builder()
                .name(originalFilename)
                .path(newPath)
                .suffix(filesEntity.getSuffix())
                .mimeType(mimeType)
                .size(size)
                .storeType(ossProperties.getType().name())
                .build();
        return new FileUploadResult(
                String.valueOf(filesInsEntity.getId()),
                url,
                meta);
    }

    /**
     * 探测文件MimeType
     *
     * @param file 文件
     * @param filename 文件名
     * @return MimeType
     */
    private String detectMimeType(MultipartFile file, String filename) {
        try {
            return tika.detect(file.getInputStream(), filename);
        } catch (Exception e) {
            log.warn("Failed to detect content type for {}: {}", filename, e.getMessage());
            return null;
        }
    }

    /**
     * 上传文件到OSS
     *
     * @param file 文件
     * @param path 存储路径
     * @param originalFilename 原始文件名
     * @return 文件访问URL
     */
    private String uploadToOss(MultipartFile file, String path, String originalFilename) {
        OssUploadRequest request = new OssUploadRequest();
        try {
            request.setInputStream(file.getInputStream());
        } catch (IOException e) {
            log.error("Failed to get input stream for file: {}", originalFilename, e);
            throw new RuntimeException("Failed to get input stream", e);
        }
        request.setPath(path);
        request.setOriginalFilename(originalFilename);
        return ossClient.upload(request);
    }

    /**
     * 保存文件物理信息
     *
     * @param path 存储路径
     * @param originalFilename 原始文件名
     * @param mimeType 文件类型
     * @param size 文件大小
     * @param md5 文件MD5
     * @return 保存后的AiFiles对象
     */
    private FilesEntity saveAiFiles(String path, String originalFilename, String mimeType, long size, String md5) {
        FilesEntity filesEntity = new FilesEntity();
        filesEntity.setPath(path);
        filesEntity.setSuffix(FileUtil.getSuffix(originalFilename));
        filesEntity.setMimeType(mimeType);
        filesEntity.setSize((int) size);
        filesEntity.setStoreType(ossProperties.getType().name());
        filesEntity.setMd5(md5);
        filesMapper.insert(filesEntity);
        return filesEntity;
    }

    /**
     * 保存文件实例信息
     *
     * @param name 文件名
     * @param url 文件URL
     * @param fileId 物理文件ID
     * @return 保存后的AiFilesIns对象
     */
    private FilesInsEntity saveAiFilesIns(String name, String url, Integer fileId) {
        FilesInsEntity filesInsEntity = new FilesInsEntity();
        filesInsEntity.setName(name);
        filesInsEntity.setUrl(url);
        filesInsEntity.setFileId(fileId);
        filesInsMapper.insert(filesInsEntity);
        return filesInsEntity;
    }
}
