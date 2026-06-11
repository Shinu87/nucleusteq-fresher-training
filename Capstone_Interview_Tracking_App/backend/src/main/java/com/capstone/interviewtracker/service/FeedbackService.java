package com.capstone.interviewtracker.service;

import java.util.List;

import com.capstone.interviewtracker.dto.Request.FeedbackRequestDTO;
import com.capstone.interviewtracker.dto.Response.FeedbackResponseDTO;

/**
 * Service interface for managing interview feedback.
 * Provides operations for submitting and retrieving feedback
 * for interviews and candidates.
 */
public interface FeedbackService {

    /**
     * Submits feedback for a given interview.
     *
     * @param request feedback request containing ratings, comments, and details
     * @return submitted feedback response DTO
     */
    FeedbackResponseDTO submitFeedback(FeedbackRequestDTO request, String email);

    /**
     * Retrieves all feedback submitted for a specific interview.
     *
     * @param interviewId interview ID
     * @param email       authenticated user email used for role-based filtering
     * @return list of feedback response DTOs
     */
    List<FeedbackResponseDTO> getFeedbackByInterview(Long interviewId, String email);

    /**
     * Retrieves all feedback submitted for a candidate across interviews.
     *
     * @param candidateId candidate ID
     * @return list of feedback response DTOs
     */
    List<FeedbackResponseDTO> getFeedbackByCandidate(Long candidateId);

    /**
     * Checks whether a panel member has already submitted feedback
     * for a given interview.
     *
     * @param interviewId interview ID
     * @param panelId     panel member ID
     * @return true if feedback already exists, false otherwise
     */
    boolean hasFeedbackSubmitted(Long interviewId, Long panelId);
}
