package com.ytx.ai.agent.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ytx.ai.agent.dto.WorkflowTrialRunRequest;
import com.ytx.ai.agent.entity.SkillEntity;
import com.ytx.ai.agent.manager.SkillManager;
import com.ytx.ai.agent.service.SkillService;
import com.ytx.ai.agent.vo.PageSearchVO;
import com.ytx.ai.agent.vo.PageVO;
import com.ytx.ai.base.exception.BizException;
import com.ytx.ai.web.vo.Response;
import com.ytx.ai.workflow.WorkflowOutput;
import com.ytx.ai.workflow.execute.FlowExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/skill")
public class SkillController {


    @Autowired
    private SkillManager skillManager;
    @Autowired
    private SkillService skillService;

    @Autowired
    private FlowExecutor flowExecutor;

    @PostMapping("/upsert")
    public Integer upsert(@RequestBody SkillEntity skill){
        return skillService.upsertSkill(skill);
    }

    @PostMapping("/update/description")
    public int updateDescription(@RequestBody SkillEntity skill) throws BizException {
        return skillService.updateDescription(skill);
    }

    @GetMapping("/find")
    public SkillEntity findSkill(@RequestParam("id") Integer skillId){
        return skillService.findSkill(skillId);
    }

    @PostMapping("/query")
    public PageVO<SkillEntity> querySkills(@RequestBody PageSearchVO<SkillEntity> skillSearchVO){
        return skillService.querySkill(skillSearchVO);
    }

    @PostMapping("/delete")
    public boolean deleteSkill(@RequestBody SkillEntity skill){
        return skillService.deleteSkill(skill.getId());
    }


    /**
     * 试运行工作流
     * @param req 试运行请求参数，包含技能定义和运行参数
     * @return 工作流执行结果
     */
    @PostMapping("/trial/run")
    public Response<WorkflowOutput> trialRun(@RequestBody WorkflowTrialRunRequest req) {
        try {
            // 校验工作流定义是否为空
            if (ObjectUtil.isEmpty(req.getDefinition())) {
                return Response.error("工作流定义不能为空");
            }
            // 构造SkillEntity
            SkillEntity skillEntity = new SkillEntity();
            skillEntity.setName(req.getName());
            skillEntity.setDescription(req.getDescription());
            skillEntity.setType(req.getType());
            skillEntity.setDefinition(req.getDefinition());
            // 执行工作流
            Object outputObj =skillManager.run(skillEntity,req.getParams(),"workflow");
            WorkflowOutput workflowOutput=null;
            if(ObjectUtil.isNotEmpty(outputObj) && outputObj instanceof WorkflowOutput){
                workflowOutput=(WorkflowOutput)outputObj;
            }
            return Response.success(workflowOutput);
        } catch (Exception e) {
            return Response.error("试运行工作流失败:" + e.getMessage());
        }
    }

}