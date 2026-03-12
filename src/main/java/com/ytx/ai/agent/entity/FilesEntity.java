package com.ytx.ai.agent.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ai_files")
public class FilesEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String path;
    private String suffix;
    private String mimeType;
    private Integer size;
    private String storeType;
    private String md5;
}
