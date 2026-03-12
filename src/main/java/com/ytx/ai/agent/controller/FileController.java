package com.ytx.ai.agent.controller;

import com.ytx.ai.agent.dto.FileData;
import com.ytx.ai.agent.dto.FileUploadResult;
import com.ytx.ai.agent.entity.FilesEntity;
import com.ytx.ai.agent.entity.FilesInsEntity;
import com.ytx.ai.agent.service.FileService;
import com.ytx.ai.parser.service.DocumentHandleService;
import com.ytx.ai.parser.vo.DocumentParseOption;
import com.ytx.ai.parser.vo.DocumentVO;
import com.ytx.ai.web.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private DocumentHandleService documentHandleService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<FileUploadResult> uploadFile(
            @RequestPart("file") MultipartFile file
    ) {
        FileUploadResult result=fileService.uploadFile(file);
        return Response.success(result);
    }

    @DeleteMapping("/{id}")
    public Response<Boolean> deleteFile(@PathVariable Integer id) {
        return Response.success(fileService.deleteFile(id));
    }

    @GetMapping
    public Response<List<FilesInsEntity>> listFiles() {
        return Response.success(fileService.listFileInstances());
    }

    @GetMapping("/md5/{md5}")
    public Response<FilesEntity> getFileByMd5(@PathVariable String md5) {
        return Response.success(fileService.getFileByMd5(md5));
    }

    @PostMapping(value = "/parse")
    public DocumentVO parseFile(@RequestBody DocumentParseOption parseOption){
        parseOption.init();
        DocumentVO documentVO= documentHandleService.documentParser(parseOption);
        return documentVO;
    }

    @PostMapping(value = "/conversions/base64")
    public Response<String> toBase64(@RequestBody FileData fileData){
        String base64= documentHandleService.toBase64WithScheme(fileData.getBytes());
        return Response.success(base64);
    }
}
