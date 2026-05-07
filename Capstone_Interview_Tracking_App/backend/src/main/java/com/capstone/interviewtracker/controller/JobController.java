package com.capstone.interviewtracker.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.interviewtracker.constants.api.HrApiConstants;
import com.capstone.interviewtracker.dto.Request.JobRequestDTO;
import com.capstone.interviewtracker.dto.Response.JobResponseDTO;
import com.capstone.interviewtracker.service.JobService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Handles all job related API requests.
 * Supports creating, fetching, and updating job status.
 */
@RestController
@RequestMapping(HrApiConstants.JOBS_BASE_PATH)
public class JobController {

    private final JobService jobService;

    /**
     * Initializes controller with job service.
     *
     * @param jobService service for job logic
     */
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Creates a new job description.
     *
     * @param request job request data
     * @return created job response
     */
    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<JobResponseDTO> createJob(
            @Valid @RequestBody JobRequestDTO request) {

        return ResponseEntity.ok(jobService.createJob(request));
    }

    /**
     * Fetches all job descriptions.
     *
     * @return list of job responses
     */
    @PreAuthorize("hasRole('HR')")
    @GetMapping
    public ResponseEntity<List<JobResponseDTO>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    /**
     * Returns only active jobs.
     * Used for candidate view.
     *
     * @return list of active job responses
     */
    @GetMapping(HrApiConstants.JOBS_ACTIVE)
    public ResponseEntity<List<JobResponseDTO>> getActiveJobs() {
        return ResponseEntity.ok(jobService.getActiveJobs());
    }

    /**
     * Deactivates a job without deleting it.
     * Data is still preserved in the system.
     *
     * @param id job id
     * @return updated job response
     */
    @PreAuthorize("hasRole('HR')")
    @PutMapping(HrApiConstants.JOBS_DEACTIVATE)
    public ResponseEntity<JobResponseDTO> deactivateJob(
            @PathVariable Long id) {

        return ResponseEntity.ok(jobService.deactivateJob(id));
    }

    /**
     * Activates a previously deactivated job.
     *
     * @param id job id
     * @return updated job response
     */
    @PreAuthorize("hasRole('HR')")
    @PutMapping(HrApiConstants.JOBS_ACTIVATE)
    public ResponseEntity<JobResponseDTO> activateJob(
            @PathVariable Long id) {

        return ResponseEntity.ok(jobService.activateJob(id));
    }

}