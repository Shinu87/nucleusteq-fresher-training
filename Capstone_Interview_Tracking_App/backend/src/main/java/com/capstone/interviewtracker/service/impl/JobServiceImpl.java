package com.capstone.interviewtracker.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

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

        logger.info("Create job request received for title: {}", request.getTitle());

        logger.debug("Validating experience range - min: {}, max: {}", request.getMinExperience(),
                request.getMaxExperience());

        if (request.getMinExperience() < 0 || request.getMaxExperience() < 0) {
            logger.warn("Create job failed - experience values are negative for title: {}", request.getTitle());
            throw new BadRequestException(
                    ValidationMessages.EXPERIENCE_VALUES_CANNOT_BE_NEGATIVE);
        }

        if (request.getMinExperience() > request.getMaxExperience()) {
            logger.warn("Create job failed - minExperience {} is greater than maxExperience {} for title: {}",
                    request.getMinExperience(), request.getMaxExperience(), request.getTitle());
            throw new BadRequestException(
                    ValidationMessages.MIN_CANNOT_BE_GREATER_THAN_MAX);
        }

        logger.debug("Validating salary range - min: {}, max: {}", request.getMinSalary(), request.getMaxSalary());

        if (request.getMinSalary() < 0 || request.getMaxSalary() < 0) {
            logger.warn("Create job failed - salary values are negative for title: {}", request.getTitle());
            throw new BadRequestException(
                    ValidationMessages.SALARY_VALUES_CANNOT_BE_NEGATIVE);
        }

        if (request.getMinSalary() > request.getMaxSalary()) {
            logger.warn("Create job failed - minSalary {} is greater than maxSalary {} for title: {}",
                    request.getMinSalary(), request.getMaxSalary(), request.getTitle());
            throw new BadRequestException(
                    ValidationMessages.MIN_CANNOT_BE_GREATER_THAN_MAX);
        }

        logger.debug("Checking if skill IDs are provided in job request for title: {}", request.getTitle());

        if (request.getSkillIds() == null || request.getSkillIds().isEmpty()) {
            logger.warn("Create job failed - no skills selected for title: {}", request.getTitle());
            throw new BadRequestException(
                    ValidationMessages.SKILL_SELECTION_REQUIRED);
        }

        logger.debug("Building new JobDescription object for title: {}", request.getTitle());

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

        logger.debug("Fetching {} skill(s) from database by provided IDs", request.getSkillIds().size());

        List<Skill> skillList = request.getSkillIds().stream()
                .map(id -> skillRepository.findById(id)
                        .orElseThrow(() -> {
                            logger.warn("Create job failed - skill not found for ID: {}", id);
                            return new ResourceNotFoundException(SkillMessages.SKILL_NOT_FOUND + ": " + id);
                        }))
                .collect(Collectors.toList());

        logger.debug("All {} skill(s) fetched successfully for job title: {}", skillList.size(), request.getTitle());

        job.setSkills(skillList);

        logger.debug("Saving new job to database for title: {}", request.getTitle());
        JobDescription saved = jobRepo.save(job);
        logger.info("Job created successfully with ID: {} and title: {}", saved.getId(), saved.getTitle());

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

        logger.info("Fetching all jobs (active and inactive) from the database");

        logger.debug("Calling jobRepo.findAllWithSkills()");

        List<JobResponseDTO> jobs = jobRepo.findAllWithSkills().stream()
                .map(this::mapToResponse)
                .toList();

        logger.info("Successfully fetched {} job(s) from the database", jobs.size());

        return jobs;
    }

    /**
     * Fetches only active jobs available for candidates.
     * Used in job browsing screens.
     *
     * @return list of active JobResponseDTOs
     */
    @Override
    public List<JobResponseDTO> getActiveJobs() {

        logger.info("Fetching all active jobs from the database");

        logger.debug("Calling jobRepo.findAllActiveWithSkills()");

        List<JobResponseDTO> activeJobs = jobRepo.findAllActiveWithSkills().stream()
                .map(this::mapToResponse)
                .toList();

        logger.info("Successfully fetched {} active job(s) from the database", activeJobs.size());

        return activeJobs;
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

        logger.info("Deactivate job request received for jobId: {}", jobId);

        logger.debug("Looking up job in database for jobId: {}", jobId);
        JobDescription job = jobRepo.findById(jobId)
                .orElseThrow(() -> {
                    logger.warn("Deactivate job failed - job not found for ID: {}", jobId);
                    return new ResourceNotFoundException(JobMessages.JOB_NOT_FOUND + " with id: " + jobId);
                });

        if (!job.isActive()) {
            logger.warn("Deactivate job failed - job is already inactive for ID: {}", jobId);
            throw new BadRequestException(
                    JobMessages.JOB_NOT_ACTIVE);
        }

        logger.debug("Setting job to inactive and saving for jobId: {}", jobId);
        job.setActive(false);

        JobDescription saved = jobRepo.save(job);
        logger.info("Job deactivated successfully for jobId: {}", jobId);

        return mapToResponse(saved);
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

        logger.info("Activate job request received for jobId: {}", jobId);

        logger.debug("Looking up job in database for jobId: {}", jobId);
        JobDescription job = jobRepo.findById(jobId)
                .orElseThrow(() -> {
                    logger.warn("Activate job failed - job not found for ID: {}", jobId);
                    return new ResourceNotFoundException(JobMessages.JOB_NOT_FOUND + " with id: " + jobId);
                });

        if (job.isActive()) {
            logger.warn("Activate job failed - job is already active for ID: {}", jobId);
            throw new ConflictException(
                    JobMessages.JOB_ALREADY_ACTIVE);
        }

        logger.debug("Setting job to active and saving for jobId: {}", jobId);
        job.setActive(true);

        JobDescription saved = jobRepo.save(job);
        logger.info("Job activated successfully for jobId: {}", jobId);

        return mapToResponse(saved);
    }

    /**
     * Maps JobDescription entity to JobResponseDTO.
     *
     * @param job job entity from database
     * @return mapped JobResponseDTO
     */
    private JobResponseDTO mapToResponse(JobDescription job) {

        logger.debug("Mapping job entity to response DTO for jobId: {}", job.getId());

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