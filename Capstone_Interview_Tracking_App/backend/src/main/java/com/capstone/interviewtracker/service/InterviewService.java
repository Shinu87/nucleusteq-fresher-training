package com.capstone.interviewtracker.service;

import java.util.List;

import com.capstone.interviewtracker.dto.Request.InterviewRequestDTO;
import com.capstone.interviewtracker.dto.Response.InterviewResponseDTO;

/**
 * Service interface for managing interviews.
 * Handles scheduling, status updates, and retrieval of interview data.
 */
public interface InterviewService {

    /**
     * Schedules a new interview for a candidate.
     * Assigns panel members and triggers notification emails.
     *
     * @param request interview scheduling request containing candidate,
     *                stage, time, and panel details
     * @return scheduled interview response DTO
     */
    InterviewResponseDTO scheduleInterview(InterviewRequestDTO request);

    /**
     * Marks an interview as completed.
     * Once completed, panels are allowed to submit feedback.
     *
     * @param interviewId interview ID
     * @return updated interview response DTO
     */
    InterviewResponseDTO markCompleted(Long interviewId);

    /**
     * Retrieves all interviews in the system.
     *
     * @return list of all interview response DTOs
     */
    List<InterviewResponseDTO> getAllInterviews();

    /**
     * Retrieves all interviews for a specific candidate.
     *
     * @param candidateId candidate ID
     * @return list of interview response DTOs
     */
    List<InterviewResponseDTO> getInterviewsByCandidate(Long candidateId);

    /**
     * Retrieves a single interview by its ID.
     *
     * @param id interview ID
     * @return interview response DTO
     */
    InterviewResponseDTO getInterviewById(Long id);
}