package com.ytx.ai.agent.manager;

import cn.hutool.core.util.ObjectUtil;
import com.ytx.ai.agent.entity.SkillEntity;
import com.ytx.ai.agent.service.SkillService;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SkillManager {
    @Autowired
    private SkillService skillService;

    @Autowired
    private FlowExecutor flowExecutor;

    public Object run(Integer skillId, Map<String, Object> params,String type){
        SkillEntity skill= skillService.findSkill(skillId);
        if(ObjectUtil.isEmpty(skill)){
            return null;
        }
        return run(skill,params,type);
    }

    public Object run(SkillEntity skill, Map<String, Object> params,String type){
        if(ObjectUtil.isEmpty(skill)){
            return null;
        }
        // 转换为工作流
        Workflow workflow = Workflow.of(skill);
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
        return workflowOutput;
    }
}
