package com.ytx.ai.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ai_files_ins")
public class FilesInsEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String url;
    private String name;
    private Integer fileId;
}
