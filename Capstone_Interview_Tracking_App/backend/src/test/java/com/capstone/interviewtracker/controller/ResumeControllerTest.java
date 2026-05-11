package com.capstone.interviewtracker.controller;

import com.capstone.interviewtracker.config.TestSecurityConfig;
import com.capstone.interviewtracker.exception.advice.CustomGlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    void downloadResume_pathOutsideUploadsDir_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/resumes/download")
                .param("path", "etc/passwd"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void downloadResume_pathTraversalAttempt_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/resumes/download")
                .param("path", "../../etc/passwd"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void downloadResume_validPathButFileMissing_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/resumes/download")
                .param("path", "uploads/resumes/does-not-exist.pdf"))
                .andExpect(status().isNotFound());
    }
}