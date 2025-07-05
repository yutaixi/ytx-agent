package com.ytx.ai.agent.controller;

import com.ytx.ai.agent.MasterAgent;
import com.ytx.ai.agent.dto.ChatDTO;
import com.ytx.ai.agent.vo.AgentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
public class AgentChatController {

    @Autowired
    MasterAgent masterAgent;

    @PostMapping("/chat")
    public AgentResponse chat(@RequestBody ChatDTO chatDTO){
        return masterAgent.run(chatDTO);
    }

}
