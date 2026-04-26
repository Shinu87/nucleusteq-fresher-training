package com.capstone.interviewtracker.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.interviewtracker.model.Skill;
import com.capstone.interviewtracker.repository.SkillRepository;

/**
 * Controller for getting skills data.
 */
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillRepository skillRepository;

    public SkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * Returns list of all skills
     */

    @GetMapping
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }
}
