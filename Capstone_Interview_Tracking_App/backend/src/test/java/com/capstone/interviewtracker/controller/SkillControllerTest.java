package com.capstone.interviewtracker.controller;

import com.capstone.interviewtracker.config.TestSecurityConfig;
import com.capstone.interviewtracker.dto.Response.SkillResponseDTO;
import com.capstone.interviewtracker.exception.advice.CustomGlobalExceptionHandler;
import com.capstone.interviewtracker.service.SkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for SkillController.
 */
@WebMvcTest(controllers = SkillController.class)
@ContextConfiguration(classes = {
                SkillController.class,
                TestSecurityConfig.class,
                CustomGlobalExceptionHandler.class
})
class SkillControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private SkillService skillService;

        /**
         * Helper to build a sample skill response DTO.
         */
        private SkillResponseDTO skill(Long id, String name) {
                SkillResponseDTO dto = new SkillResponseDTO();
                dto.setId(id);
                dto.setName(name);
                return dto;
        }

        /**
         * Tests that HR can fetch all skills.
         */
        @Test
        @WithMockUser(roles = "HR")
        void testGetAllSkillsAsHr() throws Exception {
                when(skillService.getAllSkills())
                                .thenReturn(List.of(skill(1L, "Java"), skill(2L, "Spring Boot")));

                mockMvc.perform(get("/api/skills"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].name").value("Java"))
                                .andExpect(jsonPath("$[1].name").value("Spring Boot"));
        }

        /**
         * Tests that PANEL is forbidden from fetching skills.
         */
        @Test
        @WithMockUser(roles = "PANEL")
        void testGetAllSkillsAsPanelForbidden() throws Exception {
                mockMvc.perform(get("/api/skills"))
                                .andExpect(status().isForbidden());
        }

        /**
         * Tests that CANDIDATE is forbidden from fetching skills.
         */
        @Test
        @WithMockUser(roles = "CANDIDATE")
        void testGetAllSkillsAsCandidateForbidden() throws Exception {
                mockMvc.perform(get("/api/skills"))
                                .andExpect(status().isForbidden());
        }
}