package com.ytx.ai.agent.controller;

import com.ytx.ai.agent.entity.SkillEntity;
import com.ytx.ai.agent.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skill")
public class SkillController {


    @Autowired
    private SkillService skillService;

    @PostMapping("/upsert")
    public boolean chat(@RequestBody SkillEntity skill){
        return skillService.upsertSkill(skill);
    }
}
