package com.ytx.ai.agent.dto;

import com.ytx.ai.oss.vo.FileMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadResult {
    private String id;
    private String url;
    private FileMeta meta;
}
