package com.ytx.ai.agent.controller;

import com.ytx.ai.agent.dto.SkillToolDebugRequest;
import com.ytx.ai.agent.entity.SkillToolEntity;
import com.ytx.ai.agent.service.SkillToolService;
import com.ytx.ai.web.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 技能工具控制器
 * 提供技能工具的增删改查功能
 */
@RestController
@RequestMapping("/api/skill/tool")
public class SkillToolController {

    @Autowired
    private SkillToolService skillToolService;

    /**
     * 创建技能工具
     *
     * @param skillToolEntity 技能工具实体，包含技能工具的详细信息
     * @return boolean 创建成功返回 true，否则返回 false
     */
    @PostMapping("/create")
    public boolean createSkillTool(@RequestBody SkillToolEntity skillToolEntity) {
        // 调用 service 层创建方法保存实体
        return skillToolService.createSkillTool(skillToolEntity);
    }

    /**
     * 更新技能工具
     *
     * @param skillToolEntity 技能工具实体，必须包含 ID
     * @return boolean 更新成功返回 true，否则返回 false
     */
    @PostMapping("/update")
    public boolean updateSkillTool(@RequestBody SkillToolEntity skillToolEntity) {
        // 调用 service 层更新方法根据 ID 更新实体
        return skillToolService.updateSkillTool(skillToolEntity);
    }

    /**
     * 通过 skillId 查询返回集合
     *
     * @param skillId 技能 ID，用于筛选关联的工具
     * @return List<SkillToolEntity> 返回匹配的技能工具列表
     */
    @GetMapping("/list")
    public List<SkillToolEntity> querySkillToolListBySkillId(@RequestParam("skillId") Integer skillId) {
        // 调用 service 层根据 skillId 查询列表的方法
        return skillToolService.querySkillToolListBySkillId(skillId);
    }

    /**
     * 通过 id 查询返回单个记录
     *
     * @param id 技能工具 ID
     * @return SkillToolEntity 返回对应的技能工具实体，如果不存在则返回 null
     */
    @GetMapping("/get")
    public SkillToolEntity getSkillToolById(@RequestParam("id") Integer id) {
        // 调用 service 层根据 id 查询的方法
        return skillToolService.getSkillToolById(id);
    }

    @PostMapping("/disable")
    public boolean updateDisabled(@RequestBody SkillToolEntity skillToolEntity) {
        return skillToolService.updateDisabled(skillToolEntity.getId(), skillToolEntity.getDisabled());
    }

    /**
     * 根据 id 删除功能的接口
     *
     * @param skillToolEntity 技能工具实体，需要包含 ID
     * @return boolean 删除成功返回 true，否则返回 false
     */
    @PostMapping("/delete")
    public boolean deleteSkillToolById(@RequestBody SkillToolEntity skillToolEntity) {
        // 调用 service 层根据 id 删除的方法
        return skillToolService.deleteSkillToolById(skillToolEntity.getId());
    }


    @PostMapping("/debug")
    public Response<Object> debug(@RequestBody SkillToolDebugRequest debugRequest){
        SkillToolEntity skillToolEntity=new SkillToolEntity();
        skillToolEntity.setSkillId(debugRequest.getSkillId());
        skillToolEntity.setName(debugRequest.getName());
        skillToolEntity.setDescription(debugRequest.getDescription());
        skillToolEntity.setDefinition(debugRequest.getDefinition());
        Object result=skillToolService.debug(skillToolEntity,debugRequest.getParams());
        return Response.success(result);
    }
}