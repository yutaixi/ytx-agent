package com.ytx.ai.agent.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ytx.ai.agent.dto.TrialRunRequest;
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


    /**
     * 试运行工作流
     * @param req 试运行请求参数，包含技能定义和运行参数
     * @return 工作流执行结果
     */
    @PostMapping("/trial/run")
    public Response<WorkflowOutput> trialRun(@RequestBody TrialRunRequest req) {
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
            Map<String, Object> params = req.getParams();
            if (ObjectUtil.isNotEmpty(inputs) && ObjectUtil.isNotEmpty(params)) {
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
            return Response.error("试运行工作流失败:" + e.getMessage());
        }
    }

}
