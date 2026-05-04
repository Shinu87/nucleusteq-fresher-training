package com.capstone.interviewtracker.service;

import java.util.List;

import com.capstone.interviewtracker.dto.Request.JobRequestDTO;
import com.capstone.interviewtracker.dto.Response.JobResponseDTO;

/**
 * Service layer interface for Job operations.
 * Defines business operations related to job management.
 */
public interface JobService {

    /**
     * Creates a new job posting in the system.
     *
     * @param request job request containing title, description,
     *                experience range, salary range, and skills
     * @return created job response DTO
     */
    JobResponseDTO createJob(JobRequestDTO request);

    /**
     * Retrieves all jobs (including active and inactive).
     *
     * @return list of all job response DTOs
     */
    List<JobResponseDTO> getAllJobs();

    /**
     * Retrieves only active job postings.
     *
     * @return list of active job response DTOs
     */
    List<JobResponseDTO> getActiveJobs();

    /**
     * Deactivates a job so it no longer appears to candidates.
     *
     * @param jobId job ID
     * @return updated job response DTO
     */
    JobResponseDTO deactivateJob(Long jobId);

    /**
     * Reactivates a previously deactivated job.
     *
     * @param jobId job ID
     * @return updated job response DTO
     */
    JobResponseDTO activateJob(Long jobId);
}