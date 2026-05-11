package com.capstone.interviewtracker.controller;

import com.capstone.interviewtracker.config.TestSecurityConfig;
import com.capstone.interviewtracker.dto.Request.InterviewRequestDTO;
import com.capstone.interviewtracker.dto.Response.InterviewResponseDTO;
import com.capstone.interviewtracker.enums.InterviewStatus;
import com.capstone.interviewtracker.enums.Stage;
import com.capstone.interviewtracker.exception.advice.CustomGlobalExceptionHandler;
import com.capstone.interviewtracker.service.InterviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for InterviewController.
 */
@WebMvcTest(controllers = InterviewController.class)
@ContextConfiguration(classes = {
                InterviewController.class,
                TestSecurityConfig.class,
                CustomGlobalExceptionHandler.class
})
class InterviewControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private InterviewService interviewService;

        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
        }

        private InterviewResponseDTO sampleInterview(Long id) {
                InterviewResponseDTO dto = new InterviewResponseDTO();
                dto.setId(id);
                dto.setStage(Stage.L1);
                dto.setScheduledAt(LocalDateTime.of(2026, 6, 1, 10, 0));
                dto.setFocusArea("Java + Spring");
                dto.setCandidateId(50L);
                dto.setCandidateName("John Candidate");
                dto.setPanelIds(List.of(11L, 12L));
                dto.setPanelNames(List.of("Panel A", "Panel B"));
                dto.setStatus(InterviewStatus.SCHEDULED);
                return dto;
        }

        private InterviewRequestDTO validInterviewRequest() {
                InterviewRequestDTO req = new InterviewRequestDTO();
                req.setStage(Stage.L1);
                req.setScheduledAt(LocalDateTime.of(2026, 6, 1, 10, 0));
                req.setFocusArea("Java + Spring");
                req.setCandidateId(50L);
                req.setPanelIds(List.of(11L));
                return req;
        }

        /* scheduleInterview (HR only) */

        @Test
        @WithMockUser(roles = "HR")
        void scheduleInterview_asHr_validRequest_returnsCreated() throws Exception {
                when(interviewService.scheduleInterview(any(InterviewRequestDTO.class)))
                                .thenReturn(sampleInterview(1L));

                mockMvc.perform(post("/api/interviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validInterviewRequest())))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.stage").value("L1"))
                                .andExpect(jsonPath("$.candidateId").value(50));
        }

        @Test
        @WithMockUser(roles = "HR")
        void scheduleInterview_missingFields_returnsBadRequest() throws Exception {
                InterviewRequestDTO req = new InterviewRequestDTO();

                mockMvc.perform(post("/api/interviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "HR")
        void scheduleInterview_tooManyPanels_returnsBadRequest() throws Exception {
                InterviewRequestDTO req = validInterviewRequest();
                req.setPanelIds(List.of(11L, 12L, 13L));

                mockMvc.perform(post("/api/interviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void scheduleInterview_asPanel_returnsForbidden() throws Exception {
                mockMvc.perform(post("/api/interviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validInterviewRequest())))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CANDIDATE")
        void scheduleInterview_asCandidate_returnsForbidden() throws Exception {
                mockMvc.perform(post("/api/interviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validInterviewRequest())))
                                .andExpect(status().isForbidden());
        }

        /* markCompleted (HR only) */

        @Test
        @WithMockUser(roles = "HR")
        void markCompleted_asHr_returnsOk() throws Exception {
                InterviewResponseDTO completed = sampleInterview(1L);
                completed.setStatus(InterviewStatus.COMPLETED);
                when(interviewService.markCompleted(1L)).thenReturn(completed);

                mockMvc.perform(put("/api/interviews/1/complete"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("COMPLETED"));
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void markCompleted_asPanel_returnsForbidden() throws Exception {
                mockMvc.perform(put("/api/interviews/1/complete"))
                                .andExpect(status().isForbidden());
        }

        /* getAllInterviews (HR, PANEL) */

        @Test
        @WithMockUser(roles = "HR")
        void getAllInterviews_asHr_returnsList() throws Exception {
                when(interviewService.getAllInterviews())
                                .thenReturn(List.of(sampleInterview(1L), sampleInterview(2L)));

                mockMvc.perform(get("/api/interviews"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void getAllInterviews_asPanel_returnsList() throws Exception {
                when(interviewService.getAllInterviews()).thenReturn(List.of(sampleInterview(1L)));

                mockMvc.perform(get("/api/interviews"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @WithMockUser(roles = "CANDIDATE")
        void getAllInterviews_asCandidate_returnsForbidden() throws Exception {
                mockMvc.perform(get("/api/interviews"))
                                .andExpect(status().isForbidden());
        }

        /* getInterviewById (HR, PANEL) */

        @Test
        @WithMockUser(roles = "HR")
        void getInterviewById_asHr_returnsOk() throws Exception {
                when(interviewService.getInterviewById(1L)).thenReturn(sampleInterview(1L));

                mockMvc.perform(get("/api/interviews/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void getInterviewById_asPanel_returnsOk() throws Exception {
                when(interviewService.getInterviewById(1L)).thenReturn(sampleInterview(1L));

                mockMvc.perform(get("/api/interviews/1"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "CANDIDATE")
        void getInterviewById_asCandidate_returnsForbidden() throws Exception {
                mockMvc.perform(get("/api/interviews/1"))
                                .andExpect(status().isForbidden());
        }

        /* getInterviewsByCandidate (HR, PANEL) */

        @Test
        @WithMockUser(roles = "HR")
        void getInterviewsByCandidate_asHr_returnsList() throws Exception {
                when(interviewService.getInterviewsByCandidate(50L))
                                .thenReturn(List.of(sampleInterview(1L)));

                mockMvc.perform(get("/api/interviews/candidate/50"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].candidateId").value(50));
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void getInterviewsByCandidate_asPanel_returnsList() throws Exception {
                when(interviewService.getInterviewsByCandidate(50L))
                                .thenReturn(List.of(sampleInterview(1L)));

                mockMvc.perform(get("/api/interviews/candidate/50"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "CANDIDATE")
        void getInterviewsByCandidate_asCandidate_returnsForbidden() throws Exception {
                mockMvc.perform(get("/api/interviews/candidate/50"))
                                .andExpect(status().isForbidden());
        }
}
