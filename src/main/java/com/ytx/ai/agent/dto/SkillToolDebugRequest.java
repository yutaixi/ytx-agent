package com.ytx.ai.agent.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SkillToolDebugRequest {

    private Integer skillId;
    private String name;
    private String description;
    private String definition;

    /**
     * 试运行参数
     */
    private Map<String, Object> params;
}