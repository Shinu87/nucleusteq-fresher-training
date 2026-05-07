package com.capstone.interviewtracker.controller;

import com.capstone.interviewtracker.config.TestSecurityConfig;
import com.capstone.interviewtracker.dto.Request.CandidateRequestDTO;
import com.capstone.interviewtracker.dto.Response.ApplicationStatusDTO;
import com.capstone.interviewtracker.dto.Response.CandidateResponseDTO;
import com.capstone.interviewtracker.enums.CandidateStatus;
import com.capstone.interviewtracker.enums.Stage;
import com.capstone.interviewtracker.service.CandidateService;
import com.capstone.interviewtracker.service.ResumeStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.capstone.interviewtracker.exception.advice.CustomGlobalExceptionHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for CandidateController.
 */
@WebMvcTest(controllers = CandidateController.class)
@ContextConfiguration(classes = {
                CandidateController.class,
                TestSecurityConfig.class,
                CustomGlobalExceptionHandler.class
})
class CandidateControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private CandidateService candidateService;

        @MockBean
        private ResumeStorageService resumeStorageService;

        private CandidateResponseDTO sampleCandidate(Long id) {
                CandidateResponseDTO dto = new CandidateResponseDTO();
                dto.setId(id);
                dto.setName("shinu Candidate");
                dto.setEmail("shinu@example.com");
                dto.setPhone("9876543210");
                dto.setDateOfBirth(LocalDate.of(1995, 5, 10));
                dto.setTotalExperience(3);
                dto.setCurrentStage(Stage.SCREENING);
                dto.setStatus(CandidateStatus.IN_PROGRESS);
                dto.setJobId(100L);
                dto.setJobTitle("Backend Engineer");
                return dto;
        }

        private CandidateRequestDTO validCandidateRequest() {
                CandidateRequestDTO req = new CandidateRequestDTO();
                req.setName("shinu Candidate");
                req.setEmail("shinu@example.com");
                req.setPhone("9876543210");
                req.setDateOfBirth(LocalDate.of(1995, 5, 10));
                req.setTotalExperience(3);
                req.setCurrentStage(Stage.PROFILING);
                req.setStatus(CandidateStatus.IN_PROGRESS);
                req.setJobId(100L);
                return req;
        }

        /* createCandidate */

        @Test
        @WithMockUser(roles = "HR")
        void createCandidate_validRequest_returnsCreated() throws Exception {
                when(candidateService.createCandidate(any(CandidateRequestDTO.class)))
                                .thenReturn(sampleCandidate(1L));

                mockMvc.perform(post("/api/candidates")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validCandidateRequest())))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.email").value("shinu@example.com"))
                                .andExpect(jsonPath("$.jobTitle").value("Backend Engineer"));
        }

        @Test
        @WithMockUser(roles = "HR")
        void createCandidate_missingRequiredFields_returnsBadRequest() throws Exception {
                CandidateRequestDTO req = new CandidateRequestDTO();

                mockMvc.perform(post("/api/candidates")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "HR")
        void createCandidate_invalidEmail_returnsBadRequest() throws Exception {
                CandidateRequestDTO req = validCandidateRequest();
                req.setEmail("not-an-email");

                mockMvc.perform(post("/api/candidates")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        /* getAllCandidates (HR only) */

        @Test
        @WithMockUser(roles = "HR")
        void getAllCandidates_asHr_returnsList() throws Exception {
                when(candidateService.getAllCandidates())
                                .thenReturn(List.of(sampleCandidate(1L), sampleCandidate(2L)));

                mockMvc.perform(get("/api/candidates"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void getAllCandidates_asPanel_returnsForbidden() throws Exception {
                mockMvc.perform(get("/api/candidates"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CANDIDATE")
        void getAllCandidates_asCandidate_returnsForbidden() throws Exception {
                mockMvc.perform(get("/api/candidates"))
                                .andExpect(status().isForbidden());
        }

        /* getMyApplication */

        @Test
        @WithMockUser(username = "shinu@example.com", roles = "CANDIDATE")
        void getMyApplication_validEmail_returnsOk() throws Exception {
                ApplicationStatusDTO dto = new ApplicationStatusDTO();
                dto.setCandidateId(1L);
                dto.setCandidateEmail("shinu@example.com");
                dto.setApplicationStatus(CandidateStatus.IN_PROGRESS);

                when(candidateService.getApplicationStatusByEmail("shinu@example.com")).thenReturn(dto);

                mockMvc.perform(get("/api/candidates/me/application")
                                .param("email", "shinu@example.com"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.candidateId").value(1))
                                .andExpect(jsonPath("$.candidateEmail").value("shinu@example.com"));
        }

        @Test
        @WithMockUser(username = "shinu@example.com", roles = "CANDIDATE")
        void getMyApplication_blankEmail_returnsBadRequest() throws Exception {
                mockMvc.perform(get("/api/candidates/me/application")
                                .param("email", ""))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "shinu@example.com", roles = "CANDIDATE")
        void getMyApplication_emailMismatch_returnsForbidden() throws Exception {
                mockMvc.perform(get("/api/candidates/me/application")
                                .param("email", "other@example.com"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "missing@example.com", roles = "CANDIDATE")
        void getMyApplication_emailNotFound_returnsNotFound() throws Exception {
                when(candidateService.getApplicationStatusByEmail(anyString())).thenReturn(null);

                mockMvc.perform(get("/api/candidates/me/application")
                                .param("email", "missing@example.com"))
                                .andExpect(status().isNotFound());
        }

        /* uploadResume */

        @Test
        @WithMockUser(username = "shinu@example.com", roles = "CANDIDATE")
        void uploadResume_validFile_returnsOk() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "file", "resume.pdf", MediaType.APPLICATION_PDF_VALUE, "fake-pdf".getBytes());

                when(resumeStorageService.storeResume(any())).thenReturn("uploads/resumes/abc_resume.pdf");
                // updateResumePath now takes 3 args: candidateId, resumePath, loggedInEmail
                when(candidateService.updateResumePath(eq(1L), anyString(), anyString()))
                                .thenReturn(sampleCandidate(1L));

                mockMvc.perform(multipart("/api/candidates/1/resume").file(file))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        /* reApply (CANDIDATE only) */

        @Test
        @WithMockUser(username = "shinu@example.com", roles = "CANDIDATE")
        void reApply_asCandidate_validBody_returnsOk() throws Exception {
                // reApply now takes 3 args: candidateId, newJobId, email
                when(candidateService.reApply(eq(1L), eq(200L), anyString())).thenReturn(sampleCandidate(1L));

                mockMvc.perform(post("/api/candidates/1/reapply")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("jobId", 200L))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @WithMockUser(roles = "CANDIDATE")
        void reApply_missingJobId_returnsBadRequest() throws Exception {
                mockMvc.perform(post("/api/candidates/1/reapply")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "HR")
        void reApply_asHr_returnsForbidden() throws Exception {
                mockMvc.perform(post("/api/candidates/1/reapply")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("jobId", 200L))))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void reApply_asPanel_returnsForbidden() throws Exception {
                mockMvc.perform(post("/api/candidates/1/reapply")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("jobId", 200L))))
                                .andExpect(status().isForbidden());
        }

        /* getCandidateById */

        @Test
        @WithMockUser(roles = "HR")
        void getCandidateById_returnsOk() throws Exception {
                when(candidateService.getCandidateById(1L)).thenReturn(sampleCandidate(1L));

                mockMvc.perform(get("/api/candidates/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("shinu Candidate"));
        }

        /* advanceStage (HR only) */

        @Test
        @WithMockUser(roles = "HR")
        void advanceStage_asHr_returnsOk() throws Exception {
                when(candidateService.advanceStage(1L)).thenReturn(sampleCandidate(1L));

                mockMvc.perform(put("/api/candidates/1/advance"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void advanceStage_asPanel_returnsForbidden() throws Exception {
                mockMvc.perform(put("/api/candidates/1/advance"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CANDIDATE")
        void advanceStage_asCandidate_returnsForbidden() throws Exception {
                mockMvc.perform(put("/api/candidates/1/advance"))
                                .andExpect(status().isForbidden());
        }

        /* rejectCandidate (HR only) */

        @Test
        @WithMockUser(roles = "HR")
        void rejectCandidate_asHr_returnsOk() throws Exception {
                CandidateResponseDTO rejected = sampleCandidate(1L);
                rejected.setStatus(CandidateStatus.REJECTED);
                when(candidateService.rejectCandidate(1L)).thenReturn(rejected);

                mockMvc.perform(put("/api/candidates/1/reject"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("REJECTED"));
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void rejectCandidate_asPanel_returnsForbidden() throws Exception {
                mockMvc.perform(put("/api/candidates/1/reject"))
                                .andExpect(status().isForbidden());
        }
}