package com.capstone.interviewtracker.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.capstone.interviewtracker.dto.Request.JobRequestDTO;
import com.capstone.interviewtracker.dto.Response.JobResponseDTO;
import com.capstone.interviewtracker.model.JobDescription;
import com.capstone.interviewtracker.repository.JobDescriptionRepository;
import com.capstone.interviewtracker.service.JobService;

/**
 * Service implementation for Job related operations.
 * Handles business logic between controller and repository.
 */
@Service
public class JobServiceImpl implements JobService {

    private final JobDescriptionRepository jobRepo;

    public JobServiceImpl(JobDescriptionRepository jobRepo) {
        this.jobRepo = jobRepo;
    }

    /**
     * Creates a new job and saves it into database.
     */
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

        return response;
    }

    /**
     * Returns all jobs from database.
     */
    @Override
    public List<JobResponseDTO> getAllJobs() {
        return jobRepo.findAll().stream().map(job -> {

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

            return dto;
        }).toList();
    }
}