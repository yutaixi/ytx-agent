package com.ytx.ai.agent.controller;

import com.ytx.ai.base.agent.AgentResponse;
import com.ytx.ai.base.agent.ChatDTO;
import com.ytx.ai.workflow.service.AgentChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
public class AgentChatController {


    @Autowired
    private AgentChatService agentChatService;


    @PostMapping("/chat")
    public AgentResponse chat(@RequestBody ChatDTO chatDTO){
        return agentChatService.run(chatDTO);
    }

}
