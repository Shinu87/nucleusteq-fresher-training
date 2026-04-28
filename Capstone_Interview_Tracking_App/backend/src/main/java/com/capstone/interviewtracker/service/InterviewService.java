package com.capstone.interviewtracker.service;

import java.util.List;

import com.capstone.interviewtracker.dto.Request.InterviewRequestDTO;
import com.capstone.interviewtracker.dto.Response.InterviewResponseDTO;

/**
 * interview service layer
 * handles interview scheduling and management
 */
public interface InterviewService {

    /**
     * schedule interview for candidate
     * assign panels and send emails
     */
    InterviewResponseDTO scheduleInterview(InterviewRequestDTO request);

    /**
     * get all interviews
     */
    List<InterviewResponseDTO> getAllInterviews();

    /**
     * get interviews for a candidate
     */
    List<InterviewResponseDTO> getInterviewsByCandidate(Long candidateId);

    /**
     * get interview by id
     */
    InterviewResponseDTO getInterviewById(Long id);
}