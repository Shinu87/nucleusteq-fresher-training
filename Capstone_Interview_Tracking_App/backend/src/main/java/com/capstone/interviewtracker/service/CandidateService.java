package com.capstone.interviewtracker.service;

import java.util.List;

import com.capstone.interviewtracker.dto.Request.CandidateRequestDTO;
import com.capstone.interviewtracker.dto.Response.ApplicationStatusDTO;
import com.capstone.interviewtracker.dto.Response.CandidateResponseDTO;

/**
 * Service interface for managing candidate operations.
 * Provides methods for candidate creation, status tracking,
 * stage progression, and application management.
 */
public interface CandidateService {

    /**
     * Creates a new candidate application.
     *
     * @param request candidate creation request
     * @return created candidate response DTO
     */
    CandidateResponseDTO createCandidate(CandidateRequestDTO request);

    /**
     * Retrieves application status of a candidate by email.
     *
     * @param email candidate email
     * @return application status DTO
     */
    ApplicationStatusDTO getApplicationStatusByEmail(String email);

    /**
     * Re-applies a candidate for a new job after rejection.
     *
     * @param candidateId candidate ID
     * @param newJobId    new job ID
     * @param email       authenticated user email for ownership verification
     * @return updated candidate response DTO
     */
    CandidateResponseDTO reApply(Long candidateId, Long newJobId, String email);

    /**
     * Updates resume file path for a candidate.
     *
     * @param candidateId   candidate ID
     * @param resumePath    file path of uploaded resume
     * @param loggedInEmail authenticated user email used for ownership verification
     * @return updated candidate response DTO
     */
    CandidateResponseDTO updateResumePath(
            Long candidateId,
            String resumePath,
            String loggedInEmail);

    /**
     * Retrieves all candidates in the system.
     *
     * @return list of candidate response DTOs
     */
    List<CandidateResponseDTO> getAllCandidates();

    /**
     * Retrieves candidate details by ID.
     *
     * @param id candidate ID
     * @return candidate response DTO
     */
    CandidateResponseDTO getCandidateById(Long id);

    /**
     * Advances candidate to the next interview stage.
     *
     * @param candidateId candidate ID
     * @return updated candidate response DTO
     */
    CandidateResponseDTO advanceStage(Long candidateId);

    /**
     * Rejects a candidate application.
     *
     * @param candidateId candidate ID
     * @return updated candidate response DTO
     */
    CandidateResponseDTO rejectCandidate(Long candidateId);
}