package com.capstone.interviewtracker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.capstone.interviewtracker.constants.api.PanelApiConstants;
import com.capstone.interviewtracker.dto.Request.FeedbackRequestDTO;
import com.capstone.interviewtracker.dto.Response.FeedbackResponseDTO;
import com.capstone.interviewtracker.service.FeedbackService;

import jakarta.validation.Valid;

/**
 * Handles all feedback related API requests.
 */
@RestController
@RequestMapping(PanelApiConstants.BASE_PATH)
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * Constructor for FeedbackController.
     *
     * @param feedbackService service for feedback logic
     */
    public FeedbackController(final FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * Submits feedback for an interview.
     *
     * @param request feedback request data
     * @return created feedback response
     */
    // @PreAuthorize("hasAnyRole('HR','PANEL')")
    // @PostMapping
    // public ResponseEntity<FeedbackResponseDTO> submitFeedback(
    // @Valid @RequestBody final FeedbackRequestDTO request) {

    // FeedbackResponseDTO response = feedbackService.submitFeedback(request);

    // return ResponseEntity.status(HttpStatus.CREATED).body(response);
    // }
    @PreAuthorize("hasAnyRole('HR', 'PANEL')")
    @PostMapping
    public ResponseEntity<FeedbackResponseDTO> submitFeedback(
            @Valid @RequestBody final FeedbackRequestDTO request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedbackService.submitFeedback(request, email));
    }

    /**
     * Checks whether feedback is already submitted.
     *
     * @param interviewId interview id
     * @param panelId     panel member id
     * @return map showing submission status
     */
    @PreAuthorize("hasAnyRole('HR', 'PANEL')")
    @GetMapping(PanelApiConstants.CHECK)
    public ResponseEntity<Map<String, Boolean>> checkFeedbackStatus(
            @RequestParam final Long interviewId,
            @RequestParam final Long panelId) {

        boolean submitted = feedbackService.hasFeedbackSubmitted(interviewId, panelId);

        return ResponseEntity.ok(Map.of("submitted", submitted));
    }

    /**
     * Returns all feedback for a given interview.
     *
     * @param interviewId interview id
     * @return list of feedback responses
     */
    @PreAuthorize("hasAnyRole('HR', 'PANEL')")
    @GetMapping(PanelApiConstants.BY_INTERVIEW)
    public ResponseEntity<List<FeedbackResponseDTO>> getFeedbackByInterview(
            @PathVariable final Long interviewId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return ResponseEntity.ok(
                feedbackService.getFeedbackByInterview(interviewId, email));
    }

    /**
     * Returns all feedback for a candidate.
     *
     * @param candidateId candidate id
     * @return list of feedback responses
     */
    @PreAuthorize("hasRole('HR')")
    @GetMapping(PanelApiConstants.BY_CANDIDATE)
    public ResponseEntity<List<FeedbackResponseDTO>> getFeedbackByCandidate(
            @PathVariable final Long candidateId) {

        return ResponseEntity.ok(
                feedbackService.getFeedbackByCandidate(candidateId));
    }
}