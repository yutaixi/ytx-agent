package com.ytx.ai.agent.dto;

import java.util.Map;

/**
 * 技能试运行请求参数
 */
public class TrialRunRequest {

    /**
     * 技能名称
     */
    private String name;

    /**
     * 技能描述
     */
    private String description;

    /**
     * 技能类型
     */
    private String type;

    /**
     * 技能定义(Workflow JSON字符串)
     */
    private String definition;

    /**
     * 试运行参数
     */
    private Map<String, Object> params;

    /**
     * 获取技能名称
     * @return 技能名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置技能名称
     * @param name 技能名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取技能描述
     * @return 技能描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置技能描述
     * @param description 技能描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取技能类型
     * @return 技能类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置技能类型
     * @param type 技能类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取技能定义(Workflow JSON字符串)
     * @return 技能定义(Workflow JSON字符串)
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * 设置技能定义(Workflow JSON字符串)
     * @param definition 技能定义(Workflow JSON字符串)
     */
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    /**
     * 获取试运行参数
     * @return 试运行参数
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * 设置试运行参数
     * @param params 试运行参数
     */
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}

