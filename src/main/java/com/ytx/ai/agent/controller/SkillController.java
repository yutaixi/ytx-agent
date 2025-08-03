package com.ytx.ai.agent.controller;

import com.ytx.ai.agent.entity.SkillEntity;
import com.ytx.ai.agent.service.SkillService;
import com.ytx.ai.agent.vo.PageSearchVO;
import com.ytx.ai.agent.vo.PageVO;
import com.ytx.ai.base.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/skill")
public class SkillController {


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
}
