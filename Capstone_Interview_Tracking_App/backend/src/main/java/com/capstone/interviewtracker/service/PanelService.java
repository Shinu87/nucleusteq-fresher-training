package com.capstone.interviewtracker.service;

import java.util.List;

import com.capstone.interviewtracker.dto.Request.PanelRequestDTO;
import com.capstone.interviewtracker.dto.Response.PanelResponseDTO;

/**
 * Service layer for Panel operations
 * (contains business logic methods for panel)
 */
public interface PanelService {
    /**
     * create new panel member
     */
    PanelResponseDTO createPanel(PanelRequestDTO request);

    /**
     * get all panels
     */
    List<PanelResponseDTO> getAllPanels();

    /**
     * get panel by id
     */
    PanelResponseDTO getPanelById(Long id);

    /**
     * activate panel
     */
    PanelResponseDTO activatePanel(Long panelId);
}