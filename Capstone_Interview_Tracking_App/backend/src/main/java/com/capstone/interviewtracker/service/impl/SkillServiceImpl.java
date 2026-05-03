package com.capstone.interviewtracker.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.capstone.interviewtracker.dto.Response.SkillResponseDTO;
import com.capstone.interviewtracker.repository.SkillRepository;
import com.capstone.interviewtracker.service.SkillService;

/**
 * Service implementation for handling skill-related operations.
 */
@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    /**
     * Constructor for SkillServiceImpl.
     *
     * @param skillRepository repository for skill data access
     */
    public SkillServiceImpl(final SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * Fetches all skills from database and maps to response DTO.
     *
     * @return list of skill response DTOs
     */
    @Override
    public List<SkillResponseDTO> getAllSkills() {
        return skillRepository.findAll()
                .stream()
                .map(skill -> {
                    final SkillResponseDTO dto = new SkillResponseDTO();
                    dto.setId(skill.getId());
                    dto.setName(skill.getName());
                    return dto;
                })
                .toList();
    }
}