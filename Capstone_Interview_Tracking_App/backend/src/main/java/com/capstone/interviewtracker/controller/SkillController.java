package com.capstone.interviewtracker.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.interviewtracker.constants.api.HrApiConstants;
import com.capstone.interviewtracker.dto.Response.SkillResponseDTO;
import com.capstone.interviewtracker.service.SkillService;

/**
 * Controller for handling skill-related operations.
 */
@RestController
@RequestMapping(HrApiConstants.SKILLS_BASE_PATH)
public class SkillController {

    private final SkillService skillService;

    /**
     * Constructor for SkillController.
     *
     * @param skillService service for skill operations
     */
    public SkillController(final SkillService skillService) {
        this.skillService = skillService;
    }

    /**
     * Retrieves all skills.
     *
     * @return list of skills
     */
    @GetMapping
    public ResponseEntity<List<SkillResponseDTO>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }
}