package com.capstone.interviewtracker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.capstone.interviewtracker.constants.api.CandidateApiConstants;
import com.capstone.interviewtracker.dto.Request.CandidateRequestDTO;
import com.capstone.interviewtracker.dto.Response.ApplicationStatusDTO;
import com.capstone.interviewtracker.dto.Response.CandidateResponseDTO;
import com.capstone.interviewtracker.service.CandidateService;
import com.capstone.interviewtracker.service.ResumeStorageService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller class for handling Candidate-related APIs.
 */
@RestController
@RequestMapping(CandidateApiConstants.BASE_PATH)
public class CandidateController {

    private final CandidateService candidateService;
    private final ResumeStorageService resumeStorageService;

    public CandidateController(CandidateService candidateService,
            ResumeStorageService resumeStorageService) {
        this.candidateService = candidateService;
        this.resumeStorageService = resumeStorageService;
    }

    /**
     * This API is used to create a new candidate.
     *
     * @param request candidate request data
     * @return created candidate response
     */
    @PreAuthorize("hasAnyRole('HR','CANDIDATE')")
    @PostMapping()
    public ResponseEntity<CandidateResponseDTO> createCandidate(@Valid @RequestBody CandidateRequestDTO request) {
        CandidateResponseDTO response = candidateService.createCandidate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * This API returns candidate details.
     *
     * @return list of candidates
     */
    @PreAuthorize("hasRole('HR')")
    @GetMapping
    public ResponseEntity<List<CandidateResponseDTO>> getAllCandidates() {
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    /**
     * Allows candidate to check application status using email.
     *
     * @param email the candidate email
     * @return application status response
     */
    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping(CandidateApiConstants.ME_APPLICATION)
    public ResponseEntity<ApplicationStatusDTO> getMyApplication(
            @RequestParam(name = "email") String email) {

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = auth != null ? auth.getName() : null;

        if (loggedInEmail == null || !loggedInEmail.equalsIgnoreCase(email.trim())) {
            return ResponseEntity.status(403).build();
        }

        ApplicationStatusDTO dto = candidateService.getApplicationStatusByEmail(email.trim());

        if (dto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dto);
    }

    /**
     * Uploads resume for a candidate.
     * File is stored and path is updated in database.
     * 
     * @param id   candidate id
     * @param file resume file
     * @return updated candidate response
     */
    @PreAuthorize("hasAnyRole('CANDIDATE','HR')")
    @PostMapping(CandidateApiConstants.RESUME)
    public ResponseEntity<CandidateResponseDTO> uploadResume(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        String storedPath = resumeStorageService.storeResume(file);

        CandidateResponseDTO response = candidateService.updateResumePath(id, storedPath, email);

        return ResponseEntity.ok(response);
    }

    /**
     * Allows candidate to reapply after rejection.
     * Works only when status is REJECTED.
     *
     * @param id   candidate id
     * @param body request body containing jobId
     * @return updated candidate response
     */
    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping(CandidateApiConstants.REAPPLY)
    public ResponseEntity<CandidateResponseDTO> reApply(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Long newJobId = body.get("jobId");

        if (newJobId == null) {
            return ResponseEntity.badRequest().build();
        }

        CandidateResponseDTO response = candidateService.reApply(id, newJobId, email);

        return ResponseEntity.ok(response);
    }

    /**
     * Returns candidate details by id.
     *
     * @param id candidate id
     * @return candidate details
     */
    @PreAuthorize("hasAnyRole('HR','PANEL')")
    @GetMapping(CandidateApiConstants.BY_ID)
    public ResponseEntity<CandidateResponseDTO> getCandidateById(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.getCandidateById(id));
    }

    /**
     * Moves candidate to next stage.
     * Used by HR during interview process.
     * 
     * @param id candidate id
     * @return updated candidate response
     */
    @PreAuthorize("hasRole('HR')")
    @PutMapping(CandidateApiConstants.ADVANCE)
    public ResponseEntity<CandidateResponseDTO> advanceStage(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.advanceStage(id));
    }

    /**
     * Rejects a candidate.
     *
     * @param id candidate id
     * @return updated candidate response
     */
    @PreAuthorize("hasRole('HR')")
    @PutMapping(CandidateApiConstants.REJECT)
    public ResponseEntity<CandidateResponseDTO> rejectCandidate(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.rejectCandidate(id));
    }

}