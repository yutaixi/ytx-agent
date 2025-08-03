package com.ytx.ai.agent.controller;

import com.ytx.ai.agent.entity.AgentEntity;
import com.ytx.ai.agent.service.AgentService;
import com.ytx.ai.agent.vo.PageSearchVO;
import com.ytx.ai.agent.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @PostMapping("/upsert")
    public Integer upsert(@RequestBody AgentEntity agent){
        return agentService.upsertAgent(agent);
    }

    @GetMapping("/find")
    public AgentEntity findAgent(@RequestParam("id") Integer agentId){
        return agentService.findAgent(agentId);
    }

    @PostMapping("/query")
    public PageVO<AgentEntity> queryAgents(@RequestBody PageSearchVO<AgentEntity> agentSearchVO){
        return agentService.queryAgentPage(agentSearchVO);
    }

    @PostMapping("/delete")
    public boolean deleteAgent(@RequestBody AgentEntity agent){
        return agentService.deleteAgent(agent.getId());
    }
}
