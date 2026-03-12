package com.ytx.ai.agent.service;

import com.ytx.ai.agent.dto.FileUploadResult;
import com.ytx.ai.agent.entity.FilesEntity;
import com.ytx.ai.agent.entity.FilesInsEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    /**
     * Add a file. Checks MD5, creates FilesEntity if needed, creates FilesInsEntity.
     * @param filesEntity Storage info (must include MD5, path, etc.)
     * @param name File name for the instance
     * @param url URL for the instance
     * @return The created FilesInsEntity
     */
    FilesInsEntity addFile(FilesEntity filesEntity, String name, String url);

    /**
     * Delete file instance by ID.
     * @param id FilesInsEntity ID
     * @return true if deleted
     */
    boolean deleteFile(Integer id);

    /**
     * Query file instance by ID.
     * @param id FilesInsEntity ID
     * @return FilesInsEntity
     */
    FilesInsEntity getFileInstance(Integer id);

    /**
     * Query all file instances.
     * @return List of FilesInsEntity
     */
    List<FilesInsEntity> listFileInstances();

    /**
     * Query storage info by MD5.
     * @param md5 File MD5
     * @return FilesEntity
     */
    FilesEntity getFileByMd5(String md5);

    /**
     * Upload file with MD5 check.
     * @param file Multipart file
     * @return FileUploadResult
     */
    FileUploadResult uploadFile(MultipartFile file);
}
