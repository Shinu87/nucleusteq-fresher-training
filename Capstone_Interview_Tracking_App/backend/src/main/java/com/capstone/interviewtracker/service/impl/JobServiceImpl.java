package com.capstone.interviewtracker.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.capstone.interviewtracker.dto.Request.JobRequestDTO;
import com.capstone.interviewtracker.dto.Response.JobResponseDTO;
import com.capstone.interviewtracker.model.JobDescription;
import com.capstone.interviewtracker.repository.JobDescriptionRepository;
import com.capstone.interviewtracker.service.JobService;

import com.capstone.interviewtracker.model.Skill;
import com.capstone.interviewtracker.repository.SkillRepository;
import java.util.stream.Collectors;

/**
 * Service implementation for Job related operations.
 * Handles business logic between controller and repository.
 */
@Service
public class JobServiceImpl implements JobService {

    private final JobDescriptionRepository jobRepo;
    private final SkillRepository skillRepository;

    public JobServiceImpl(JobDescriptionRepository jobRepo, SkillRepository skillRepository) {
        this.jobRepo = jobRepo;
        this.skillRepository = skillRepository;
    }

    @Override
    public JobResponseDTO createJob(JobRequestDTO request) {

        JobDescription job = new JobDescription();

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setMinExperience(request.getMinExperience());
        job.setMaxExperience(request.getMaxExperience());
        job.setMinSalary(request.getMinSalary());
        job.setMaxSalary(request.getMaxSalary());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());

        // converting skill IDs to Skill objects
        List<Skill> skillList = request.getSkillIds()
                .stream()
                .map(id -> {
                    return skillRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Skill not found: " + id));
                })
                .collect(Collectors.toList());
        job.setSkills(skillList);

        JobDescription saved = jobRepo.save(job);

        JobResponseDTO response = new JobResponseDTO();

        response.setId(saved.getId());
        response.setTitle(saved.getTitle());
        response.setDescription(saved.getDescription());
        response.setMinExperience(saved.getMinExperience());
        response.setMaxExperience(saved.getMaxExperience());
        response.setMinSalary(saved.getMinSalary());
        response.setMaxSalary(saved.getMaxSalary());
        response.setLocation(saved.getLocation());
        response.setJobType(saved.getJobType());

        // extracting skill names for response
        List<String> skillNames = saved.getSkills()
                .stream()
                .map(Skill::getName)
                .toList();

        response.setSkills(skillNames);
        return response;
    }

    /**
     * Fetches all jobs along with their skills.
     * Uses JOIN FETCH to avoid lazy loading issues.
     */
    @Override
    public List<JobResponseDTO> getAllJobs() {
        return jobRepo.findAllWithSkills().stream().map(job -> {
            JobResponseDTO dto = new JobResponseDTO();
            dto.setId(job.getId());
            dto.setTitle(job.getTitle());
            dto.setDescription(job.getDescription());
            dto.setMinExperience(job.getMinExperience());
            dto.setMaxExperience(job.getMaxExperience());
            dto.setMinSalary(job.getMinSalary());
            dto.setMaxSalary(job.getMaxSalary());
            dto.setLocation(job.getLocation());
            dto.setJobType(job.getJobType());

            List<String> skillNames = job.getSkills()
                    .stream()
                    .map(Skill::getName)
                    .toList();

            dto.setSkills(skillNames);

            return dto;
        }).toList();
    }
}