package com.capstone.interviewtracker.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.capstone.interviewtracker.constants.messages.JobMessages;
import com.capstone.interviewtracker.constants.messages.SkillMessages;
import com.capstone.interviewtracker.constants.messages.ValidationMessages;
import com.capstone.interviewtracker.dto.Request.JobRequestDTO;
import com.capstone.interviewtracker.dto.Response.JobResponseDTO;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
import com.capstone.interviewtracker.exception.custom.BadRequestException;
import com.capstone.interviewtracker.exception.custom.ConflictException;
import com.capstone.interviewtracker.model.JobDescription;
import com.capstone.interviewtracker.model.Skill;
import com.capstone.interviewtracker.repository.JobDescriptionRepository;
import com.capstone.interviewtracker.repository.SkillRepository;
import com.capstone.interviewtracker.service.JobService;

/**
 * Service implementation for Job related operations.
 */
@Service
public class JobServiceImpl implements JobService {

    private final JobDescriptionRepository jobRepo;
    private final SkillRepository skillRepository;

    /**
     * Constructor injection for JobService dependencies.
     *
     * @param jobRepo         repository for job descriptions
     * @param skillRepository repository for skills
     */
    public JobServiceImpl(JobDescriptionRepository jobRepo,
            SkillRepository skillRepository) {

        this.jobRepo = jobRepo;
        this.skillRepository = skillRepository;
    }

    /**
     * Creates a new job posting.
     * Validates experience, salary, and required skills before saving.
     *
     * @param request job creation request data
     * @return created job as JobResponseDTO
     */
    @Override
    public JobResponseDTO createJob(JobRequestDTO request) {

        // basic numeric sanity validations
        if (request.getMinExperience() < 0 || request.getMaxExperience() < 0) {
            throw new BadRequestException(
                    ValidationMessages.EXPERIENCE_VALUES_CANNOT_BE_NEGATIVE);
        }

        if (request.getMinExperience() > request.getMaxExperience()) {
            throw new BadRequestException(
                    ValidationMessages.MIN_CANNOT_BE_GREATER_THAN_MAX);
        }

        if (request.getMinSalary() < 0 || request.getMaxSalary() < 0) {
            throw new BadRequestException(
                    ValidationMessages.SALARY_VALUES_CANNOT_BE_NEGATIVE);
        }

        if (request.getMinSalary() > request.getMaxSalary()) {
            throw new BadRequestException(
                    ValidationMessages.MIN_CANNOT_BE_GREATER_THAN_MAX);
        }

        // at least one skill must be selected
        if (request.getSkillIds() == null || request.getSkillIds().isEmpty()) {
            throw new BadRequestException(
                    ValidationMessages.SKILL_SELECTION_REQUIRED);
        }

        JobDescription job = new JobDescription();
        job.setTitle(request.getTitle().trim());
        job.setDescription(request.getDescription().trim());
        job.setMinExperience(request.getMinExperience());
        job.setMaxExperience(request.getMaxExperience());
        job.setMinSalary(request.getMinSalary());
        job.setMaxSalary(request.getMaxSalary());
        job.setLocation(request.getLocation().trim());
        job.setJobType(request.getJobType());
        job.setActive(true);

        List<Skill> skillList = request.getSkillIds().stream()
                .map(id -> skillRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                SkillMessages.SKILL_NOT_FOUND + ": " + id)))
                .collect(Collectors.toList());

        job.setSkills(skillList);

        JobDescription saved = jobRepo.save(job);

        return mapToResponse(saved);
    }

    /**
     * Fetches all jobs including active and inactive ones.
     * Used in HR dashboard for full job visibility.
     *
     * @return list of all JobResponseDTOs
     */
    @Override
    public List<JobResponseDTO> getAllJobs() {

        return jobRepo.findAllWithSkills().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Fetches only active jobs available for candidates.
     * Used in job browsing screens.
     *
     * @return list of active JobResponseDTOs
     */
    @Override
    public List<JobResponseDTO> getActiveJobs() {

        return jobRepo.findAllActiveWithSkills().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Deactivates a job without deleting it.
     * The job data is preserved for audit and history purposes.
     *
     * @param jobId unique identifier of the job
     * @return updated JobResponseDTO after deactivation
     */
    @Override
    public JobResponseDTO deactivateJob(Long jobId) {

        JobDescription job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        JobMessages.JOB_NOT_FOUND + " with id: " + jobId));

        if (!job.isActive()) {
            throw new BadRequestException(
                    JobMessages.JOB_NOT_ACTIVE);
        }

        job.setActive(false);

        return mapToResponse(jobRepo.save(job));
    }

    /**
     * Activates a previously deactivated job.
     * The job will become visible again in candidate job listings.
     *
     * @param jobId unique identifier of the job
     * @return updated JobResponseDTO after activation
     */
    @Override
    public JobResponseDTO activateJob(Long jobId) {

        JobDescription job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        JobMessages.JOB_NOT_FOUND + " with id: " + jobId));

        if (job.isActive()) {
            throw new ConflictException(
                    JobMessages.JOB_ALREADY_ACTIVE);
        }

        job.setActive(true);

        return mapToResponse(jobRepo.save(job));
    }

    /**
     * Maps JobDescription entity to JobResponseDTO.
     *
     * @param job job entity from database
     * @return mapped JobResponseDTO
     */
    private JobResponseDTO mapToResponse(JobDescription job) {

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
        dto.setActive(job.isActive());

        List<String> skillNames = job.getSkills()
                .stream()
                .map(Skill::getName)
                .toList();

        dto.setSkills(skillNames);

        return dto;
    }
}
