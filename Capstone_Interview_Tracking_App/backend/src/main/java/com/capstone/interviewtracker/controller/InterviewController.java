package com.capstone.interviewtracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.interviewtracker.constants.api.HrApiConstants;
import com.capstone.interviewtracker.dto.Request.InterviewRequestDTO;
import com.capstone.interviewtracker.dto.Response.InterviewResponseDTO;
import com.capstone.interviewtracker.service.InterviewService;

import jakarta.validation.Valid;

/**
 * Handles all interview related API requests.
 * Supports scheduling and fetching interview details.
 */
@RestController
@RequestMapping(HrApiConstants.INTERVIEWS_BASE_PATH)
public class InterviewController {

    private final InterviewService interviewService;

    /**
     * Initializes controller with interview service.
     *
     * @param interviewService service for interview logic
     */
    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    /**
     * Schedules an interview for a candidate.
     *
     * @param request interview request data
     * @return created interview response
     */
    @PreAuthorize("hasRole('HR')")
    @PostMapping
    public ResponseEntity<InterviewResponseDTO> scheduleInterview(
            @Valid @RequestBody InterviewRequestDTO request) {

        InterviewResponseDTO response = interviewService.scheduleInterview(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Marks an interview as completed.
     *
     * @param id interview id
     * @return updated interview response
     */
    @PreAuthorize("hasRole('HR')")
    @PutMapping(HrApiConstants.INTERVIEWS_COMPLETE)
    public ResponseEntity<InterviewResponseDTO> markCompleted(
            @PathVariable Long id) {

        InterviewResponseDTO response = interviewService.markCompleted(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Returns all interviews.
     *
     * @return list of interview responses
     */
    @PreAuthorize("hasAnyRole('HR', 'PANEL')")
    @GetMapping
    public ResponseEntity<List<InterviewResponseDTO>> getAllInterviews() {
        return ResponseEntity.ok(interviewService.getAllInterviews());
    }

    /**
     * Returns interview details by id.
     *
     * @param id interview id
     * @return interview response
     */
    @PreAuthorize("hasAnyRole('HR', 'PANEL')")
    @GetMapping(HrApiConstants.INTERVIEWS_BY_ID)
    public ResponseEntity<InterviewResponseDTO> getInterviewById(
            @PathVariable Long id) {

        return ResponseEntity.ok(interviewService.getInterviewById(id));
    }

    /**
     * Returns all interviews for a candidate.
     *
     * @param candidateId candidate id
     * @return list of interview responses
     */
    @PreAuthorize("hasAnyRole('HR', 'PANEL')")
    @GetMapping(HrApiConstants.INTERVIEWS_BY_CANDIDATE)
    public ResponseEntity<List<InterviewResponseDTO>> getInterviewsByCandidate(
            @PathVariable Long candidateId) {

        return ResponseEntity.ok(
                interviewService.getInterviewsByCandidate(candidateId));
    }
}