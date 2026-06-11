package com.capstone.interviewtracker.controller;

import com.capstone.interviewtracker.config.TestSecurityConfig;
import com.capstone.interviewtracker.dto.Request.FeedbackRequestDTO;
import com.capstone.interviewtracker.dto.Response.FeedbackResponseDTO;
import com.capstone.interviewtracker.enums.FeedbackStatus;
import com.capstone.interviewtracker.enums.Stage;
import com.capstone.interviewtracker.exception.advice.CustomGlobalExceptionHandler;
import com.capstone.interviewtracker.service.FeedbackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for FeedbackController.
 */
@WebMvcTest(controllers = FeedbackController.class)
@ContextConfiguration(classes = {
                FeedbackController.class,
                TestSecurityConfig.class,
                CustomGlobalExceptionHandler.class
})
class FeedbackControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private FeedbackService feedbackService;

        /**
         * Helper to build a sample feedback response DTO.
         */
        private FeedbackResponseDTO sampleFeedback(Long id) {
                FeedbackResponseDTO dto = new FeedbackResponseDTO();
                dto.setId(id);
                dto.setComments("Solid candidate");
                dto.setStrengths("Strong fundamentals");
                dto.setWeaknesses("Needs more system design practice");
                dto.setAreasCovered("DSA, Spring Boot");
                dto.setRating(4);
                dto.setStatus(FeedbackStatus.SELECTED);
                dto.setInterviewId(10L);
                dto.setInterviewStage(Stage.L1);
                dto.setPanelId(20L);
                dto.setPanelName("Panel A");
                return dto;
        }

        /**
         * Helper to build a valid feedback request DTO.
         */
        private FeedbackRequestDTO validFeedbackRequest() {
                FeedbackRequestDTO req = new FeedbackRequestDTO();
                req.setComments("Solid candidate");
                req.setStrengths("Strong fundamentals");
                req.setWeaknesses("Needs more system design practice");
                req.setAreasCovered("DSA, Spring Boot");
                req.setRating(4);
                req.setStatus(FeedbackStatus.SELECTED);
                req.setInterviewId(10L);
                req.setPanelId(20L);
                return req;
        }

        /**
         * Tests that PANEL user can submit feedback successfully.
         */
        @Test
        @WithMockUser(username = "panel@example.com", roles = "PANEL")
        void testSubmitFeedbackAsPanel() throws Exception {
                when(feedbackService.submitFeedback(any(FeedbackRequestDTO.class), anyString()))
                                .thenReturn(sampleFeedback(1L));

                mockMvc.perform(post("/api/feedbacks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validFeedbackRequest())))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.rating").value(4))
                                .andExpect(jsonPath("$.status").value("SELECTED"));
        }

        /**
         * Tests that HR user can submit feedback successfully.
         */
        @Test
        @WithMockUser(username = "hr@example.com", roles = "HR")
        void testSubmitFeedbackAsHr() throws Exception {
                when(feedbackService.submitFeedback(any(FeedbackRequestDTO.class), anyString()))
                                .thenReturn(sampleFeedback(1L));

                mockMvc.perform(post("/api/feedbacks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validFeedbackRequest())))
                                .andExpect(status().isCreated());
        }

        /**
         * Tests that blank comments cause a bad request response.
         */
        @Test
        @WithMockUser(roles = "PANEL")
        void testSubmitFeedbackBlankComments() throws Exception {
                FeedbackRequestDTO req = validFeedbackRequest();
                req.setComments("");
                mockMvc.perform(post("/api/feedbacks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        /**
         * Tests that a rating above the allowed range is rejected.
         */
        @Test
        @WithMockUser(roles = "PANEL")
        void testSubmitFeedbackRatingTooHigh() throws Exception {
                FeedbackRequestDTO req = validFeedbackRequest();
                req.setRating(10);

                mockMvc.perform(post("/api/feedbacks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        /**
         * Tests that a rating below the allowed range is rejected.
         */
        @Test
        @WithMockUser(roles = "PANEL")
        void testSubmitFeedbackRatingTooLow() throws Exception {
                FeedbackRequestDTO req = validFeedbackRequest();
                req.setRating(0);

                mockMvc.perform(post("/api/feedbacks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        /**
         * Tests that a missing panel id is rejected.
         */
        @Test
        @WithMockUser(roles = "PANEL")
        void testSubmitFeedbackMissingPanelId() throws Exception {
                FeedbackRequestDTO req = validFeedbackRequest();
                req.setPanelId(null);

                mockMvc.perform(post("/api/feedbacks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        /**
         * Tests that a CANDIDATE is forbidden from submitting feedback.
         */
        @Test
        @WithMockUser(roles = "CANDIDATE")
        void testSubmitFeedbackAsCandidateForbidden() throws Exception {
                mockMvc.perform(post("/api/feedbacks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validFeedbackRequest())))
                                .andExpect(status().isForbidden());
        }

        /**
         * Tests checking feedback status returns true when feedback exists.
         */
        @Test
        @WithMockUser(roles = "PANEL")
        void testCheckFeedbackStatusTrue() throws Exception {
                when(feedbackService.hasFeedbackSubmitted(10L, 20L)).thenReturn(true);

                mockMvc.perform(get("/api/feedbacks/check")
                                .param("interviewId", "10")
                                .param("panelId", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.submitted").value(true));
        }

        /**
         * Tests checking feedback status returns false when feedback is missing.
         */
        @Test
        @WithMockUser(roles = "HR")
        void testCheckFeedbackStatusFalse() throws Exception {
                when(feedbackService.hasFeedbackSubmitted(10L, 20L)).thenReturn(false);

                mockMvc.perform(get("/api/feedbacks/check")
                                .param("interviewId", "10")
                                .param("panelId", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.submitted").value(false));
        }

        /**
         * Tests that a CANDIDATE cannot check feedback status.
         */
        @Test
        @WithMockUser(roles = "CANDIDATE")
        void testCheckFeedbackStatusAsCandidateForbidden() throws Exception {
                mockMvc.perform(get("/api/feedbacks/check")
                                .param("interviewId", "10")
                                .param("panelId", "20"))
                                .andExpect(status().isForbidden());
        }

        /**
         * Tests that PANEL can fetch feedback list by interview id.
         * getFeedbackByInterview now takes (interviewId, email) - the controller
         * extracts the email from SecurityContext and passes it to the service.
         */
        @Test
        @WithMockUser(username = "panel@example.com", roles = "PANEL")
        void testGetFeedbackByInterviewAsPanel() throws Exception {
                when(feedbackService.getFeedbackByInterview(eq(10L), anyString()))
                                .thenReturn(List.of(sampleFeedback(1L), sampleFeedback(2L)));

                mockMvc.perform(get("/api/feedbacks/interview/10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(2));
        }

        /**
         * Tests that HR can fetch feedback list by interview id.
         */
        @Test
        @WithMockUser(username = "hr@example.com", roles = "HR")
        void testGetFeedbackByInterviewAsHr() throws Exception {
                when(feedbackService.getFeedbackByInterview(eq(10L), anyString()))
                                .thenReturn(List.of(sampleFeedback(1L)));

                mockMvc.perform(get("/api/feedbacks/interview/10"))
                                .andExpect(status().isOk());
        }

        /**
         * Tests that a CANDIDATE cannot view feedback for an interview.
         */
        @Test
        @WithMockUser(roles = "CANDIDATE")
        void testGetFeedbackByInterviewAsCandidateForbidden() throws Exception {
                mockMvc.perform(get("/api/feedbacks/interview/10"))
                                .andExpect(status().isForbidden());
        }

        /**
         * Tests that HR can fetch feedback list by candidate id.
         */
        @Test
        @WithMockUser(roles = "HR")
        void testGetFeedbackByCandidateAsHr() throws Exception {
                when(feedbackService.getFeedbackByCandidate(50L))
                                .thenReturn(List.of(sampleFeedback(1L)));

                mockMvc.perform(get("/api/feedbacks/candidate/50"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1));
        }

        /**
         * Tests that PANEL is forbidden from fetching feedback by candidate id.
         */
        @Test
        @WithMockUser(roles = "PANEL")
        void testGetFeedbackByCandidateAsPanelForbidden() throws Exception {
                mockMvc.perform(get("/api/feedbacks/candidate/50"))
                                .andExpect(status().isForbidden());
        }

        /**
         * Tests that CANDIDATE is forbidden from fetching feedback by candidate id.
         */
        @Test
        @WithMockUser(roles = "CANDIDATE")
        void testGetFeedbackByCandidateAsCandidateForbidden() throws Exception {
                mockMvc.perform(get("/api/feedbacks/candidate/50"))
                                .andExpect(status().isForbidden());
        }
}