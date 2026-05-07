package com.capstone.interviewtracker.controller;

import com.capstone.interviewtracker.config.TestSecurityConfig;
import com.capstone.interviewtracker.dto.Request.PanelRequestDTO;
import com.capstone.interviewtracker.dto.Response.PanelResponseDTO;
import com.capstone.interviewtracker.exception.advice.CustomGlobalExceptionHandler;
import com.capstone.interviewtracker.service.PanelService;
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
 * Controller tests for PanelController.
 */
@WebMvcTest(controllers = PanelController.class)
@ContextConfiguration(classes = {
                PanelController.class,
                TestSecurityConfig.class,
                CustomGlobalExceptionHandler.class
})
class PanelControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private PanelService panelService;

        private PanelResponseDTO samplePanel(Long id) {
                PanelResponseDTO dto = new PanelResponseDTO();
                dto.setId(id);
                dto.setName("Panel User");
                dto.setEmail("panel@company.com");
                dto.setMobile("9876543210");
                dto.setOrganization("ACME");
                dto.setDesignation("Senior Engineer");
                dto.setExpertise("Java, Spring");
                dto.setActive(false);
                return dto;
        }

        private PanelRequestDTO validPanelRequest() {
                PanelRequestDTO req = new PanelRequestDTO();
                req.setName("Panel User");
                req.setEmail("panel@company.com");
                req.setMobile("9876543210");
                req.setOrganization("ACME");
                req.setDesignation("Senior Engineer");
                req.setExpertise("Java, Spring");
                return req;
        }

        /* createPanel (HR only) */

        @Test
        @WithMockUser(roles = "HR")
        void createPanel_asHr_validRequest_returnsCreated() throws Exception {
                when(panelService.createPanel(any(PanelRequestDTO.class)))
                                .thenReturn(samplePanel(1L));

                mockMvc.perform(post("/api/panels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validPanelRequest())))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.email").value("panel@company.com"));
        }

        @Test
        @WithMockUser(roles = "HR")
        void createPanel_invalidEmail_returnsBadRequest() throws Exception {
                PanelRequestDTO req = validPanelRequest();
                req.setEmail("not-an-email");

                mockMvc.perform(post("/api/panels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "HR")
        void createPanel_blankRequiredField_returnsBadRequest() throws Exception {
                PanelRequestDTO req = validPanelRequest();
                req.setName(""); // fails @NotBlank

                mockMvc.perform(post("/api/panels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void createPanel_asPanel_returnsForbidden() throws Exception {
                mockMvc.perform(post("/api/panels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validPanelRequest())))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CANDIDATE")
        void createPanel_asCandidate_returnsForbidden() throws Exception {
                mockMvc.perform(post("/api/panels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validPanelRequest())))
                                .andExpect(status().isForbidden());
        }

        /* getAllPanels (HR, PANEL) */

        @Test
        @WithMockUser(roles = "HR")
        void getAllPanels_asHr_returnsList() throws Exception {
                when(panelService.getAllPanels())
                                .thenReturn(List.of(samplePanel(1L), samplePanel(2L)));

                mockMvc.perform(get("/api/panels"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void getAllPanels_asPanel_returnsList() throws Exception {
                when(panelService.getAllPanels()).thenReturn(List.of(samplePanel(1L)));

                mockMvc.perform(get("/api/panels"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "CANDIDATE")
        void getAllPanels_asCandidate_returnsForbidden() throws Exception {
                mockMvc.perform(get("/api/panels"))
                                .andExpect(status().isForbidden());
        }

        /* getPanelById (HR only) */

        @Test
        @WithMockUser(roles = "HR")
        void getPanelById_asHr_returnsOk() throws Exception {
                when(panelService.getPanelById(1L)).thenReturn(samplePanel(1L));

                mockMvc.perform(get("/api/panels/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void getPanelById_asPanel_returnsForbidden() throws Exception {
                mockMvc.perform(get("/api/panels/1"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CANDIDATE")
        void getPanelById_asCandidate_returnsForbidden() throws Exception {
                mockMvc.perform(get("/api/panels/1"))
                                .andExpect(status().isForbidden());
        }

        /* activatePanel (HR only) */

        @Test
        @WithMockUser(roles = "HR")
        void activatePanel_asHr_returnsOk() throws Exception {
                PanelResponseDTO active = samplePanel(1L);
                active.setActive(true);
                when(panelService.activatePanel(1L)).thenReturn(active);

                mockMvc.perform(put("/api/panels/1/activate"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        @WithMockUser(roles = "PANEL")
        void activatePanel_asPanel_returnsForbidden() throws Exception {
                mockMvc.perform(put("/api/panels/1/activate"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CANDIDATE")
        void activatePanel_asCandidate_returnsForbidden() throws Exception {
                mockMvc.perform(put("/api/panels/1/activate"))
                                .andExpect(status().isForbidden());
        }
}
