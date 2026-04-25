package com.capstone.interviewtracker.service;

import java.util.List;

import com.capstone.interviewtracker.dto.Request.JobRequestDTO;
import com.capstone.interviewtracker.dto.Response.JobResponseDTO;

/**
 * Service layer interface for Job operations.
 * Defines methods for job related business logic.
 */
public interface JobService {

    /**
     * Creates a new job using request data.
     */
    JobResponseDTO createJob(JobRequestDTO request);

    /**
     * Returns list of all jobs.
     */
    List<JobResponseDTO> getAllJobs();
}