package com.capstone.interviewtracker.controller;

import com.capstone.interviewtracker.config.TestSecurityConfig;
import com.capstone.interviewtracker.dto.Request.JobRequestDTO;
import com.capstone.interviewtracker.dto.Response.JobResponseDTO;
import com.capstone.interviewtracker.enums.JobType;
import com.capstone.interviewtracker.exception.advice.CustomGlobalExceptionHandler;
import com.capstone.interviewtracker.service.JobService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for JobController.
 */
@WebMvcTest(controllers = JobController.class)
@ContextConfiguration(classes = {
        JobController.class,
        TestSecurityConfig.class,
        CustomGlobalExceptionHandler.class
})
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobService jobService;

    /**
     * Helper to build a sample job response DTO.
     */
    private JobResponseDTO sampleJob(Long id, boolean active) {
        JobResponseDTO dto = new JobResponseDTO();
        dto.setId(id);
        dto.setTitle("Backend Engineer");
        dto.setDescription("Spring Boot developer");
        dto.setSkills(List.of("Java", "Spring Boot"));
        dto.setMinExperience(2);
        dto.setMaxExperience(5);
        dto.setMinSalary(800000.0);
        dto.setMaxSalary(1500000.0);
        dto.setLocation("Bangalore");
        dto.setJobType(JobType.FULL_TIME);
        dto.setActive(active);
        return dto;
    }

    /**
     * Helper to build a valid job request DTO.
     */
    private JobRequestDTO validJobRequest() {
        JobRequestDTO req = new JobRequestDTO();
        req.setTitle("Backend Engineer");
        req.setDescription("Spring Boot developer");
        req.setSkillIds(List.of(1L, 2L));
        req.setMinExperience(2);
        req.setMaxExperience(5);
        req.setMinSalary(800000.0);
        req.setMaxSalary(1500000.0);
        req.setLocation("Bangalore");
        req.setJobType(JobType.FULL_TIME);
        return req;
    }

    /**
     * Tests that HR can create a job successfully.
     */
    @Test
    @WithMockUser(roles = "HR")
    void testCreateJobAsHr() throws Exception {
        when(jobService.createJob(any(JobRequestDTO.class)))
                .thenReturn(sampleJob(1L, true));

        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validJobRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Backend Engineer"))
                .andExpect(jsonPath("$.active").value(true));
    }

    /**
     * Tests that a blank title is rejected.
     */
    @Test
    @WithMockUser(roles = "HR")
    void testCreateJobBlankTitle() throws Exception {
        JobRequestDTO req = validJobRequest();
        req.setTitle("");

        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests that missing min experience is rejected.
     */
    @Test
    @WithMockUser(roles = "HR")
    void testCreateJobMissingMinExperience() throws Exception {
        JobRequestDTO req = validJobRequest();
        req.setMinExperience(null);

        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests that PANEL is forbidden from creating a job.
     */
    @Test
    @WithMockUser(roles = "PANEL")
    void testCreateJobAsPanelForbidden() throws Exception {
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validJobRequest())))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that CANDIDATE is forbidden from creating a job.
     */
    @Test
    @WithMockUser(roles = "CANDIDATE")
    void testCreateJobAsCandidateForbidden() throws Exception {
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validJobRequest())))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that HR can fetch the list of all jobs.
     */
    @Test
    @WithMockUser(roles = "HR")
    void testGetAllJobsAsHr() throws Exception {
        when(jobService.getAllJobs())
                .thenReturn(List.of(sampleJob(1L, true), sampleJob(2L, false)));

        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    /**
     * Tests that PANEL is forbidden from fetching all jobs.
     */
    @Test
    @WithMockUser(roles = "PANEL")
    void testGetAllJobsAsPanelForbidden() throws Exception {
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that CANDIDATE is forbidden from fetching all jobs.
     */
    @Test
    @WithMockUser(roles = "CANDIDATE")
    void testGetAllJobsAsCandidateForbidden() throws Exception {
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that HR can fetch active jobs.
     */
    @Test
    @WithMockUser(roles = "HR")
    void testGetActiveJobsAsHr() throws Exception {
        when(jobService.getActiveJobs()).thenReturn(List.of(sampleJob(1L, true)));

        mockMvc.perform(get("/api/jobs/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    /**
     * Tests that PANEL can fetch active jobs.
     */
    @Test
    @WithMockUser(roles = "PANEL")
    void testGetActiveJobsAsPanel() throws Exception {
        when(jobService.getActiveJobs()).thenReturn(List.of(sampleJob(1L, true)));

        mockMvc.perform(get("/api/jobs/active"))
                .andExpect(status().isOk());
    }

    /**
     * Tests that CANDIDATE can fetch active jobs.
     */
    @Test
    @WithMockUser(roles = "CANDIDATE")
    void testGetActiveJobsAsCandidate() throws Exception {
        when(jobService.getActiveJobs()).thenReturn(List.of(sampleJob(1L, true)));

        mockMvc.perform(get("/api/jobs/active"))
                .andExpect(status().isOk());
    }

    /**
     * Tests that HR can deactivate a job successfully.
     */
    @Test
    @WithMockUser(roles = "HR")
    void testDeactivateJobAsHr() throws Exception {
        when(jobService.deactivateJob(1L)).thenReturn(sampleJob(1L, false));

        mockMvc.perform(put("/api/jobs/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    /**
     * Tests that PANEL is forbidden from deactivating a job.
     */
    @Test
    @WithMockUser(roles = "PANEL")
    void testDeactivateJobAsPanelForbidden() throws Exception {
        mockMvc.perform(put("/api/jobs/1/deactivate"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that CANDIDATE is forbidden from deactivating a job.
     */
    @Test
    @WithMockUser(roles = "CANDIDATE")
    void testDeactivateJobAsCandidateForbidden() throws Exception {
        mockMvc.perform(put("/api/jobs/1/deactivate"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that HR can activate a job successfully.
     */
    @Test
    @WithMockUser(roles = "HR")
    void testActivateJobAsHr() throws Exception {
        when(jobService.activateJob(1L)).thenReturn(sampleJob(1L, true));

        mockMvc.perform(put("/api/jobs/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    /**
     * Tests that PANEL is forbidden from activating a job.
     */
    @Test
    @WithMockUser(roles = "PANEL")
    void testActivateJobAsPanelForbidden() throws Exception {
        mockMvc.perform(put("/api/jobs/1/activate"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that CANDIDATE is forbidden from activating a job.
     */
    @Test
    @WithMockUser(roles = "CANDIDATE")
    void testActivateJobAsCandidateForbidden() throws Exception {
        mockMvc.perform(put("/api/jobs/1/activate"))
                .andExpect(status().isForbidden());
    }
}