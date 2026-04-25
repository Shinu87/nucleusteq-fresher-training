package com.capstone.interviewtracker.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.interviewtracker.dto.Request.JobRequestDTO;
import com.capstone.interviewtracker.dto.Response.JobResponseDTO;
import com.capstone.interviewtracker.service.JobService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for Job Description management APIs.
 */
@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Creates a new job description.
     */

    @PostMapping()
    public JobResponseDTO createJob(@Valid @RequestBody JobRequestDTO request) {
        return jobService.createJob(request);
    }

    /**
     * Fetches all job descriptions.
     */
    @GetMapping()
    public List<JobResponseDTO> getAllJobs() {
        return jobService.getAllJobs();
    }

}
