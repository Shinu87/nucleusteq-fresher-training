package com.capstone.interviewtracker.controller;

import com.capstone.interviewtracker.config.TestSecurityConfig;
import com.capstone.interviewtracker.exception.advice.CustomGlobalExceptionHandler;
import com.capstone.interviewtracker.model.Candidate;
import com.capstone.interviewtracker.repository.CandidateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller tests for ResumeController.
 */
@WebMvcTest(controllers = ResumeController.class)
@ContextConfiguration(classes = {
        ResumeController.class,
        TestSecurityConfig.class,
        CustomGlobalExceptionHandler.class
})
class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CandidateRepository candidateRepository;

    @Test
    @WithMockUser(roles = "HR")
    void downloadResume_pathOutsideUploadsDir_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/resumes/download")
                .param("path", "etc/passwd"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HR")
    void downloadResume_pathTraversalAttempt_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/resumes/download")
                .param("path", "../../etc/passwd"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HR")
    void downloadResume_validPathButFileMissing_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/resumes/download")
                .param("path", "uploads/resumes/does-not-exist.pdf"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "candidate@example.com", roles = "CANDIDATE")
    void downloadResume_candidateAccessesAnotherCandidateResume_returnsForbidden() throws Exception {
        Candidate candidate = new Candidate();
        candidate.setEmail("candidate@example.com");
        candidate.setResumeUrl("uploads/resumes/own_resume.pdf");

        when(candidateRepository.findByEmail("candidate@example.com"))
                .thenReturn(Optional.of(candidate));

        mockMvc.perform(get("/api/resumes/download")
                .param("path", "uploads/resumes/other_resume.pdf"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "candidate@example.com", roles = "CANDIDATE")
    void downloadResume_candidateNotFoundInRepository_returnsForbidden() throws Exception {
        when(candidateRepository.findByEmail("candidate@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/resumes/download")
                .param("path", "uploads/resumes/some_resume.pdf"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "candidate@example.com", roles = "CANDIDATE")
    void downloadResume_candidateWithNullResumeUrl_returnsForbidden() throws Exception {
        Candidate candidate = new Candidate();
        candidate.setEmail("candidate@example.com");
        candidate.setResumeUrl(null);

        when(candidateRepository.findByEmail("candidate@example.com"))
                .thenReturn(Optional.of(candidate));

        mockMvc.perform(get("/api/resumes/download")
                .param("path", "uploads/resumes/some_resume.pdf"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "candidate@example.com", roles = "CANDIDATE")
    void downloadResume_candidateAccessesOwnResume_fileMissing_returnsNotFound() throws Exception {
        String resumePath = "uploads/resumes/own_resume.pdf";

        Candidate candidate = new Candidate();
        candidate.setEmail("candidate@example.com");
        candidate.setResumeUrl(resumePath);

        when(candidateRepository.findByEmail("candidate@example.com"))
                .thenReturn(Optional.of(candidate));

        mockMvc.perform(get("/api/resumes/download")
                .param("path", resumePath))
                .andExpect(status().isNotFound());
    }
}