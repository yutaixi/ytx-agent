package com.ytx.ai.agent.manager;

import cn.hutool.core.util.ObjectUtil;
import com.ytx.ai.agent.entity.SkillEntity;
import com.ytx.ai.agent.llm.callback.StreamCallback;
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

    public Object run(SkillEntity skill, Map<String, Object> params, String type) {
        return runInternal(skill, params, null);
    }

    /**
     * 带流式回调的工作流执行入口。
     * <p>
     * 与 {@link #run(SkillEntity, Map, String)} 逻辑一致，额外将 {@code streamCallback}
     * 注入 {@link FlowContext}，使 LLM 节点和 FlowEnd 节点能够将增量内容和完成信号
     * 通过回调推送到上层（通常为 SSE 推送器）。
     *
     * @param skill          技能实体（含工作流定义 JSON）
     * @param params         工作流 Start 节点的输入参数
     * @param streamCallback 流式输出回调，每个增量片段、完成信号和异常都会通过此回调通知
     * @return 工作流执行结果（流式模式下 answer 字段通常为空，输出已通过 delta 事件推送）
     */
    public WorkflowOutput runWithStreamCallback(SkillEntity skill, Map<String, Object> params,
                                                StreamCallback streamCallback) {
        Object result = runInternal(skill, params, streamCallback);
        if (result instanceof WorkflowOutput) {
            return (WorkflowOutput) result;
        }
        return null;
    }

    /**
     * 工作流执行内部公共逻辑。
     * <p>
     * 参数说明：
     * <ul>
     *   <li>{@code skill}：技能实体，含工作流定义 JSON。</li>
     *   <li>{@code params}：Start 节点输入参数 Map，key 为变量名，value 为变量值。</li>
     *   <li>{@code streamCallback}：流式回调，为 null 时走非流式分支。</li>
     * </ul>
     *
     * @param skill          技能实体
     * @param params         Start 节点输入参数
     * @param streamCallback 流式回调（可为 null）
     * @return WorkflowOutput 或 Response.error 字符串（校验失败时）
     */
    private Object runInternal(SkillEntity skill, Map<String, Object> params, StreamCallback streamCallback) {
        if (ObjectUtil.isEmpty(skill)) {
            return null;
        }
        // 转换为工作流对象
        Workflow workflow = Workflow.of(skill);
        if (ObjectUtil.isEmpty(workflow) || ObjectUtil.isEmpty(workflow.getNodes())) {
            return Response.error("工作流构造失败");
        }

        // 创建工作流包装类
        WorkflowWrapper workflowWrapper = new WorkflowWrapper(workflow);

        // 获取 Start 节点
        FlowNode startNode = workflowWrapper.getStartNode();
        if (ObjectUtil.isEmpty(startNode)) {
            return Response.error("工作流缺少Start节点");
        }

        // 获取 Start 节点的 Meta
        FlowStart.StartNodeMeta startNodeMeta = (FlowStart.StartNodeMeta) startNode.getMeta();
        if (ObjectUtil.isEmpty(startNodeMeta)) {
            return Response.error("start节点的Meta为空");
        }

        // 将前端传入的参数注入 Start 节点的 inputs
        List<Value> inputs = startNodeMeta.getInputs();
        if (ObjectUtil.isNotEmpty(inputs) && ObjectUtil.isNotEmpty(params)) {
            inputs.forEach(input -> {
                String inputName = input.getName();
                if (ObjectUtil.isNotEmpty(inputName) && params.containsKey(inputName)) {
                    input.setContent(params.get(inputName));
                }
            });
        }

        // 创建执行上下文，流式模式下注入 streamCallback
        FlowContext flowContext = FlowContext.of();
        flowContext.setWorkflowWrapper(workflowWrapper);
        if (streamCallback != null) {
            flowContext.setStreamCallback(streamCallback);
        }

        // 执行工作流
        return flowExecutor.execute(workflow, flowContext);
    }
}