package com.capstone.interviewtracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.capstone.interviewtracker.dto.Request.FeedbackRequestDTO;
import com.capstone.interviewtracker.dto.Response.FeedbackResponseDTO;
import com.capstone.interviewtracker.enums.FeedbackStatus;
import com.capstone.interviewtracker.enums.InterviewStatus;
import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.enums.Stage;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
import com.capstone.interviewtracker.model.Feedback;
import com.capstone.interviewtracker.model.Interview;
import com.capstone.interviewtracker.model.Panel;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.FeedbackRepository;
import com.capstone.interviewtracker.repository.InterviewRepository;
import com.capstone.interviewtracker.repository.PanelRepository;
import com.capstone.interviewtracker.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for FeedbackServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private PanelRepository panelRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private FeedbackRequestDTO request;
    private Interview interview;
    private Panel panel;
    private Feedback feedback;

    /**
     * Sets up common test data before each test.
     */
    @BeforeEach
    void setUp() {
        panel = new Panel();
        panel.setId(2L);
        panel.setName("Panel A");
        panel.setEmail("panel@example.com");

        interview = new Interview();
        interview.setId(1L);
        interview.setStage(Stage.L1);
        interview.setStatus(InterviewStatus.SCHEDULED);
        /* scheduled time is in past so feedback can be submitted */
        interview.setScheduledAt(LocalDateTime.now().minusHours(1));
        interview.setPanels(List.of(panel));

        request = new FeedbackRequestDTO();
        request.setComments("Strong candidate");
        request.setStrengths("Java");
        request.setWeaknesses("DSA");
        request.setAreasCovered("Spring");
        request.setRating(4);
        request.setStatus(FeedbackStatus.SELECTED);
        request.setInterviewId(1L);
        request.setPanelId(2L);

        feedback = new Feedback();
        feedback.setId(99L);
        feedback.setComments("Strong candidate");
        feedback.setRating(4);
        feedback.setStatus(FeedbackStatus.SELECTED);
        feedback.setInterview(interview);
        feedback.setPanel(panel);
    }

    /**
     * Tests submitting feedback successfully and auto-completing the interview.
     */
    @Test
    void testSubmitFeedbackSuccess() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> inv.getArgument(0));
        when(panelRepository.findById(2L)).thenReturn(Optional.of(panel));
        when(feedbackRepository.existsByInterviewIdAndPanelId(1L, 2L)).thenReturn(false);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        FeedbackResponseDTO result = feedbackService.submitFeedback(request);

        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals(InterviewStatus.COMPLETED, interview.getStatus());
    }

    /**
     * Tests submitting feedback when interview is already completed.
     */
    @Test
    void testSubmitFeedbackInterviewAlreadyCompleted() {
        interview.setStatus(InterviewStatus.COMPLETED);
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findById(2L)).thenReturn(Optional.of(panel));
        when(feedbackRepository.existsByInterviewIdAndPanelId(1L, 2L)).thenReturn(false);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        FeedbackResponseDTO result = feedbackService.submitFeedback(request);
        assertNotNull(result);
    }

    /**
     * Tests submitting feedback when interview is not found.
     */
    @Test
    void testSubmitFeedbackInterviewNotFound() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> feedbackService.submitFeedback(request));
    }

    /**
     * Tests that feedback cannot be submitted before interview time.
     */
    @Test
    void testSubmitFeedbackTooEarly() {
        interview.setStatus(InterviewStatus.SCHEDULED);
        interview.setScheduledAt(LocalDateTime.now().plusDays(1));
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));

        assertThrows(RuntimeException.class, () -> feedbackService.submitFeedback(request));
    }

    /**
     * Tests submitting feedback when panel is not found.
     */
    @Test
    void testSubmitFeedbackPanelNotFound() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> inv.getArgument(0));
        when(panelRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> feedbackService.submitFeedback(request));
    }

    /**
     * Tests submitting feedback when panel is not assigned to the interview.
     */
    @Test
    void testSubmitFeedbackPanelNotAssigned() {
        Panel otherPanel = new Panel();
        otherPanel.setId(99L);
        interview.setPanels(List.of(otherPanel));
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> inv.getArgument(0));
        when(panelRepository.findById(2L)).thenReturn(Optional.of(panel));

        assertThrows(RuntimeException.class, () -> feedbackService.submitFeedback(request));
    }

    /**
     * Tests that submitting feedback twice for the same panel is not allowed.
     */
    @Test
    void testSubmitFeedbackAlreadySubmitted() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> inv.getArgument(0));
        when(panelRepository.findById(2L)).thenReturn(Optional.of(panel));
        when(feedbackRepository.existsByInterviewIdAndPanelId(1L, 2L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> feedbackService.submitFeedback(request));
    }

    /**
     * Tests fetching feedback list by interview id as HR - HR sees all feedback.
     * getFeedbackByInterview now takes (interviewId, email) and filters by role.
     */
    @Test
    void testGetFeedbackByInterviewAsHr() {
        User hrUser = new User();
        hrUser.setEmail("hr@example.com");
        hrUser.setRole(Role.HR);

        when(userRepository.findByEmail("hr@example.com")).thenReturn(Optional.of(hrUser));
        when(feedbackRepository.findByInterviewId(1L)).thenReturn(List.of(feedback));

        List<FeedbackResponseDTO> result = feedbackService.getFeedbackByInterview(1L, "hr@example.com");
        assertEquals(1, result.size());
        assertEquals(99L, result.get(0).getId());
    }

    /**
     * Tests fetching feedback list by interview id as PANEL - PANEL sees only own
     * feedback.
     */
    @Test
    void testGetFeedbackByInterviewAsPanel() {
        User panelUser = new User();
        panelUser.setEmail("panel@example.com");
        panelUser.setRole(Role.PANEL);

        User panelUserRef = new User();
        panelUserRef.setEmail("panel@example.com");
        panel.setUser(panelUserRef);

        when(userRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(panelUser));
        when(feedbackRepository.findByInterviewId(1L)).thenReturn(List.of(feedback));

        List<FeedbackResponseDTO> result = feedbackService.getFeedbackByInterview(1L, "panel@example.com");
        assertEquals(1, result.size());
    }

    /**
     * Tests fetching feedback list by candidate id.
     */
    @Test
    void testGetFeedbackByCandidate() {
        when(feedbackRepository.findByCandidateId(11L)).thenReturn(List.of(feedback));
        List<FeedbackResponseDTO> result = feedbackService.getFeedbackByCandidate(11L);
        assertEquals(1, result.size());
    }

    /**
     * Tests that hasFeedbackSubmitted returns true when feedback exists.
     */
    @Test
    void testHasFeedbackSubmittedTrue() {
        when(feedbackRepository.existsByInterviewIdAndPanelId(1L, 2L)).thenReturn(true);
        assertTrue(feedbackService.hasFeedbackSubmitted(1L, 2L));
    }

    /**
     * Tests that hasFeedbackSubmitted returns false when no feedback exists.
     */
    @Test
    void testHasFeedbackSubmittedFalse() {
        when(feedbackRepository.existsByInterviewIdAndPanelId(1L, 2L)).thenReturn(false);
        assertFalse(feedbackService.hasFeedbackSubmitted(1L, 2L));
    }
}