package com.capstone.interviewtracker.service;

import java.util.List;

import com.capstone.interviewtracker.dto.Request.PanelRequestDTO;
import com.capstone.interviewtracker.dto.Response.PanelResponseDTO;

/**
 * Service layer interface for Panel operations.
 * Defines business logic methods for managing panel members.
 */
public interface PanelService {

    /**
     * Creates a new panel member.
     *
     * @param request panel request containing personal and professional details
     * @return created panel response DTO
     */
    PanelResponseDTO createPanel(PanelRequestDTO request);

    /**
     * Retrieves all panel members.
     *
     * @return list of panel response DTOs
     */
    List<PanelResponseDTO> getAllPanels();

    /**
     * Retrieves a panel member by ID.
     *
     * @param id panel ID
     * @return panel response DTO
     */
    PanelResponseDTO getPanelById(Long id);

    /**
     * Activates a panel member and enables system access.
     *
     * @param panelId panel ID
     * @return updated panel response DTO
     */
    PanelResponseDTO activatePanel(Long panelId);
}