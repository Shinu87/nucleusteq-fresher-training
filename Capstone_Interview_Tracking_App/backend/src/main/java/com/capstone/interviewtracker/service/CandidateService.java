package com.capstone.interviewtracker.service;

import java.util.List;

import com.capstone.interviewtracker.dto.Request.CandidateRequestDTO;
import com.capstone.interviewtracker.dto.Response.CandidateResponseDTO;

/**
 * Service interface for Candidate module.
 */
public interface CandidateService {

    CandidateResponseDTO createCandidate(CandidateRequestDTO request);

    List<CandidateResponseDTO> getAllCandidates();

    CandidateResponseDTO getCandidateById(Long id);
}
