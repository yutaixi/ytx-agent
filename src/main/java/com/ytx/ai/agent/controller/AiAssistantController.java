package com.ytx.ai.agent.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ytx.ai.agent.dto.CodeCompletionDTO;
import com.ytx.ai.agent.manager.SkillManager;
import com.ytx.ai.web.vo.Response;
import com.ytx.ai.workflow.WorkflowOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/assistant")
public class AiAssistantController {

    @Autowired
    private SkillManager skillManager;

    @PostMapping("/code/completion")
    public Response<String> codeCompletion(@RequestBody CodeCompletionDTO codeCompletionDTO){
        Map<String,Object> params=new HashMap<>();
        params.put("code",codeCompletionDTO.getCode());
        params.put("language",codeCompletionDTO.getLanguage());
        params.put("question",codeCompletionDTO.getQuestion());
        params.put("history",codeCompletionDTO.getHistory());
        Object outputObj =skillManager.run(21,params,"workflow");
        WorkflowOutput workflowOutput=null;
        if(ObjectUtil.isNotEmpty(outputObj) && outputObj instanceof WorkflowOutput){
            workflowOutput=(WorkflowOutput)outputObj;
        }
        assert workflowOutput != null;
        return Response.success(workflowOutput.getAnswer());
    }
}
