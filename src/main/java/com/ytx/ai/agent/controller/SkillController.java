package com.ytx.ai.agent.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ytx.ai.agent.dto.WorkflowTrialRunRequest;
import com.ytx.ai.agent.entity.SkillEntity;
import com.ytx.ai.agent.manager.SkillManager;
import com.ytx.ai.agent.service.SkillService;
import com.ytx.ai.workflow.stream.WorkflowSseStreamCallback;
import com.ytx.ai.agent.vo.PageSearchVO;
import com.ytx.ai.agent.vo.PageVO;
import com.ytx.ai.base.exception.BizException;
import com.ytx.ai.web.vo.Response;
import com.ytx.ai.workflow.WorkflowOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/skill")
public class SkillController {


    @Autowired
    private SkillManager skillManager;
    @Autowired
    private SkillService skillService;

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
     * 试运行工作流（非流式）。
     * <p>
     * 同步执行工作流，所有节点完成后将 {@link WorkflowOutput} 一次性以 JSON 返回。
     * 适用于未开启流式输出（End 节点 streamOutput=false）的工作流。
     *
     * @param req 试运行请求，含工作流定义 JSON 和 Start 节点输入参数
     * @return 工作流执行结果（outputs 变量 map + answer 文本）
     */
    @PostMapping("/trial/run")
    public Response<WorkflowOutput> trialRun(@RequestBody WorkflowTrialRunRequest req) {
        try {
            if (ObjectUtil.isEmpty(req.getDefinition())) {
                return Response.error("工作流定义不能为空");
            }
            SkillEntity skillEntity = buildSkillEntity(req);
            Object outputObj = skillManager.run(skillEntity, req.getParams(), "workflow");
            WorkflowOutput workflowOutput = null;
            if (ObjectUtil.isNotEmpty(outputObj) && outputObj instanceof WorkflowOutput) {
                workflowOutput = (WorkflowOutput) outputObj;
            }
            return Response.success(workflowOutput);
        } catch (Exception e) {
            return Response.error("试运行工作流失败:" + e.getMessage());
        }
    }

    /**
     * 试运行工作流（流式 SSE）。
     * <p>
     * 以 SSE（text/event-stream）方式返回，工作流执行期间实时推送以下事件：
     * <ul>
     *   <li>{@code delta}：LLM 增量输出文本片段，前端逐字拼接展示。</li>
     *   <li>{@code answer}：FlowEnd 节点最终回答（流式 LLM 模式下通常为空）。</li>
     *   <li>{@code done}：工作流全部完成，data 为完整的 WorkflowOutput JSON。</li>
     *   <li>{@code error}：执行异常，data 为错误信息。</li>
     * </ul>
     * 适用于 End 节点 streamOutput=true 的工作流。
     *
     * @param req 试运行请求，含工作流定义 JSON 和 Start 节点输入参数
     * @return SseEmitter SSE 推送器（Spring MVC 自动保持连接直到推送器关闭）
     */
    @PostMapping("/trial/run/stream")
    public SseEmitter trialRunStream(@RequestBody WorkflowTrialRunRequest req) {
        // 创建 SSE 推送器，超时时间 3 分钟（工作流执行通常不超过此时间）
        SseEmitter sseEmitter = new SseEmitter(180_000L);

        if (ObjectUtil.isEmpty(req.getDefinition())) {
            sseEmitter.completeWithError(new IllegalArgumentException("工作流定义不能为空"));
            return sseEmitter;
        }

        WorkflowSseStreamCallback streamCallback = new WorkflowSseStreamCallback(sseEmitter);

        // 工作流执行为阻塞操作（内部依赖异步编排框架），必须在独立线程中运行，
        // 避免阻塞 Spring MVC 的 Servlet 线程，保持 SSE 连接可正常推送。
        CompletableFuture.runAsync(() -> {
            try {
                SkillEntity skillEntity = buildSkillEntity(req);
                WorkflowOutput workflowOutput = skillManager.runWithStreamCallback(
                        skillEntity, req.getParams(), streamCallback);
                // 工作流执行完毕，推送 done 事件并关闭 SSE
                streamCallback.onWorkflowDone(workflowOutput);
            } catch (Exception e) {
                streamCallback.onError(e);
            }
        });

        return sseEmitter;
    }

    /**
     * 根据试运行请求构建 SkillEntity。
     *
     * @param req 试运行请求
     * @return SkillEntity（不含 id，仅含运行所需的 name/description/type/definition）
     */
    private SkillEntity buildSkillEntity(WorkflowTrialRunRequest req) {
        SkillEntity skillEntity = new SkillEntity();
        skillEntity.setName(req.getName());
        skillEntity.setDescription(req.getDescription());
        skillEntity.setType(req.getType());
        skillEntity.setDefinition(req.getDefinition());
        return skillEntity;
    }

}