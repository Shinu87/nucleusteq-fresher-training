package com.capstone.interviewtracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.interviewtracker.dto.Request.CandidateRequestDTO;
import com.capstone.interviewtracker.dto.Response.CandidateResponseDTO;
import com.capstone.interviewtracker.service.CandidateService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller class for handling Candidate-related APIs.
 */
@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    /**
     * this API is used to create a new candidate
     * data comes from frontend and sent to service layer
     */
    @PostMapping()
    public ResponseEntity<CandidateResponseDTO> createCandidate(@Valid @RequestBody CandidateRequestDTO request) {
        CandidateResponseDTO response = candidateService.createCandidate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * this API returns candidate details
     */
    @GetMapping()
    public List<CandidateResponseDTO> getAllCandidates() {
        return candidateService.getAllCandidates();
    }

    /**
     * Returns a single candidate by ID.
     * HR sees full details; candidate can view own progress.
     */
    @GetMapping("/{id}")
    public CandidateResponseDTO getCandidateById(@PathVariable Long id) {
        return candidateService.getCandidateById(id);
    }

}
