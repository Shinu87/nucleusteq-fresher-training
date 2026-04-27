package com.capstone.interviewtracker.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.capstone.interviewtracker.exception.ResourceNotFoundException;
import com.capstone.interviewtracker.dto.Request.CandidateRequestDTO;
import com.capstone.interviewtracker.dto.Response.CandidateResponseDTO;
import com.capstone.interviewtracker.enums.CandidateStatus;
import com.capstone.interviewtracker.enums.Stage;
import com.capstone.interviewtracker.model.Candidate;
import com.capstone.interviewtracker.model.JobDescription;
import com.capstone.interviewtracker.repository.CandidateRepository;
import com.capstone.interviewtracker.repository.JobDescriptionRepository;
import com.capstone.interviewtracker.service.CandidateService;

/**
 * service class for candidate module
 * contains all main logic related to candidate like create, fetch etc
 */
@Service
public class CandidateServiceImpl implements CandidateService {
    private final CandidateRepository candidateRepository;
    private final JobDescriptionRepository jobDescriptionRepository;

    /**
     * Constructor injection for repositories.
     */
    public CandidateServiceImpl(CandidateRepository candidateRepository,
            JobDescriptionRepository jobDescriptionRepository) {
        this.candidateRepository = candidateRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
    }

    /**
     * this method is used to create a new candidate
     * it also checks duplicate email and phone before saving
     */

    @Override
    public CandidateResponseDTO createCandidate(CandidateRequestDTO request) {

        if (candidateRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Candidate with this email already exists");
        }

        if (candidateRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Candidate with this phone number already exists");
        }

        JobDescription job = jobDescriptionRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + request.getJobId()));

        Candidate candidate = new Candidate();

        candidate.setName(request.getName());
        candidate.setEmail(request.getEmail());
        candidate.setPhone(request.getPhone());
        candidate.setResumeUrl(request.getResumeUrl());
        candidate.setCurrentOrganization(request.getCurrentOrganization());
        candidate.setTotalExperience(request.getTotalExperience());
        candidate.setRelevantExperience(request.getRelevantExperience());
        candidate.setCurrentCTC(request.getCurrentCTC());
        candidate.setExpectedCTC(request.getExpectedCTC());
        candidate.setNoticePeriod(request.getNoticePeriod());
        candidate.setPreferredLocation(request.getPreferredLocation());
        candidate.setSource(request.getSource());
        candidate.setJobDescription(job);

        // Always start at PROFILING stage with IN_PROGRESS status

        candidate.setCurrentStage(Stage.PROFILING);
        candidate.setStatus(CandidateStatus.IN_PROGRESS);

        Candidate saved = candidateRepository.save(candidate);

        return mapToResponse(saved);
    }

    /**
     * this method returns all candidates from database
     */
    @Override
    public List<CandidateResponseDTO> getAllCandidates() {
        return candidateRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * this method returns candidate details using id
     * if not found then exception is thrown
     */
    @Override
    public CandidateResponseDTO getCandidateById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));
        return mapToResponse(candidate);

    }

    /**
     * converting entity to response dto
     * so we don't expose database entity directly to frontend
     */
    private CandidateResponseDTO mapToResponse(Candidate candidate) {
        CandidateResponseDTO response = new CandidateResponseDTO();
        response.setId(candidate.getId());
        response.setName(candidate.getName());
        response.setEmail(candidate.getEmail());
        response.setPhone(candidate.getPhone());
        response.setResumeUrl(candidate.getResumeUrl());
        response.setCurrentOrganization(candidate.getCurrentOrganization());
        response.setTotalExperience(candidate.getTotalExperience());
        response.setRelevantExperience(candidate.getRelevantExperience());
        response.setCurrentCTC(candidate.getCurrentCTC());
        response.setExpectedCTC(candidate.getExpectedCTC());
        response.setNoticePeriod(candidate.getNoticePeriod());
        response.setPreferredLocation(candidate.getPreferredLocation());
        response.setSource(candidate.getSource());
        response.setCurrentStage(candidate.getCurrentStage());
        response.setStatus(candidate.getStatus());
        response.setJobId(candidate.getJobDescription().getId());
        return response;
    }
}
