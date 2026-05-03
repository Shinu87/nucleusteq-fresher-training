package com.capstone.interviewtracker.service;

import java.util.List;

import com.capstone.interviewtracker.dto.Response.SkillResponseDTO;

/**
 * Service interface for managing skill-related operations.
 */
public interface SkillService {

    /**
     * Retrieves all skills from the system.
     *
     * @return list of skill response DTOs
     */
    List<SkillResponseDTO> getAllSkills();
}