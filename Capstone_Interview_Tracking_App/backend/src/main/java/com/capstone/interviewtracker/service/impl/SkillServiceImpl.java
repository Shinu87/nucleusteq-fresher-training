package com.capstone.interviewtracker.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.capstone.interviewtracker.dto.Response.SkillResponseDTO;
import com.capstone.interviewtracker.repository.SkillRepository;
import com.capstone.interviewtracker.service.SkillService;

/**
 * Service implementation for handling skill-related operations.
 */
@Service
public class SkillServiceImpl implements SkillService {

    private static final Logger logger = LoggerFactory.getLogger(SkillServiceImpl.class);

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

        logger.info("Fetching all skills from the database");

        logger.debug("Calling skillRepository.findAll()");

        List<SkillResponseDTO> skills = skillRepository.findAll()
                .stream()
                .map(skill -> {
                    logger.debug("Mapping skill to DTO - ID: {}, Name: {}", skill.getId(), skill.getName());
                    final SkillResponseDTO dto = new SkillResponseDTO();
                    dto.setId(skill.getId());
                    dto.setName(skill.getName());
                    return dto;
                })
                .toList();

        logger.info("Successfully fetched {} skill(s) from the database", skills.size());

        return skills;
    }
}