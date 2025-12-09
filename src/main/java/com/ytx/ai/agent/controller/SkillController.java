package com.ytx.ai.agent.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ytx.ai.agent.entity.SkillEntity;
import com.ytx.ai.agent.service.SkillService;
import com.ytx.ai.agent.vo.PageSearchVO;
import com.ytx.ai.agent.vo.PageVO;
import com.ytx.ai.base.exception.BizException;
import com.ytx.ai.web.vo.Response;
import com.ytx.ai.workflow.FlowNode;
import com.ytx.ai.workflow.Value;
import com.ytx.ai.workflow.Workflow;
import com.ytx.ai.workflow.WorkflowOutput;
import com.ytx.ai.workflow.execute.FlowContext;
import com.ytx.ai.workflow.execute.FlowExecutor;
import com.ytx.ai.workflow.execute.WorkflowWrapper;
import com.ytx.ai.workflow.node.internal.FlowStart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/skill")
public class SkillController {


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


    @PostMapping("/trial/run/{id}")
    public Response<WorkflowOutput> trialRun(@RequestBody Map<String, Object> params,
                                             @PathVariable("id") Integer skillId) {
        try {
            // 校验技能是否存在
            SkillEntity skillEntity = skillService.findSkill(skillId);
            if (ObjectUtil.isEmpty(skillEntity)) {
                return Response.error("未找到对应的技能，skillId：" + skillId);
            }

            // 转换为工作流
            Workflow workflow = Workflow.of(skillEntity);
            if (ObjectUtil.isEmpty(workflow) || ObjectUtil.isEmpty(workflow.getNodes())) {
                return Response.error("工作流构造失败");
            }

            // 创建工作流包装类
            WorkflowWrapper workflowWrapper = new WorkflowWrapper(workflow);

            // 获取Start节点
            FlowNode startNode = workflowWrapper.getStartNode();
            if (ObjectUtil.isEmpty(startNode)) {
                return Response.error("工作流缺少Start节点");
            }

            // 获取Start节点的Meta
            FlowStart.StartNodeMeta startNodeMeta = (FlowStart.StartNodeMeta) startNode.getMeta();
            if (ObjectUtil.isEmpty(startNodeMeta)) {
                return Response.error("start节点的Meta为空");
            }

            // 从前端传输的参数中获取并校验start节点中的inputs
            List<Value> inputs = startNodeMeta.getInputs();
            if (ObjectUtil.isNotEmpty(inputs)) {
                inputs.forEach(input -> {
                    String inputName = input.getName();
                    if (ObjectUtil.isNotEmpty(inputName) && params.containsKey(inputName)) {
                        input.setContent(params.get(inputName));
                    }
                });
            }

            // 创建执行上下文
            FlowContext flowContext = FlowContext.of();
            flowContext.setWorkflowWrapper(workflowWrapper);

            // 执行工作流
            WorkflowOutput workflowOutput = flowExecutor.execute(workflow, flowContext);

            return Response.success(workflowOutput);
        } catch (Exception e) {
            return Response.error("试运行工作流失败：" + e.getMessage());
        }
    }

}
