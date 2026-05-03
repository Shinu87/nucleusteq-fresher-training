package com.capstone.interviewtracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.interviewtracker.constants.api.HrApiConstants;
import com.capstone.interviewtracker.dto.Request.PanelRequestDTO;
import com.capstone.interviewtracker.dto.Response.PanelResponseDTO;
import com.capstone.interviewtracker.service.PanelService;

import jakarta.validation.Valid;

/**
 * Handles all panel related API requests.
 * Supports creating, fetching, and activating panel members.
 */
@RestController
@RequestMapping(HrApiConstants.PANELS_BASE_PATH)
public class PanelController {

    private final PanelService panelService;

    /**
     * Initializes controller with panel service.
     *
     * @param panelService service for panel logic
     */
    public PanelController(PanelService panelService) {
        this.panelService = panelService;
    }

    /**
     * Creates a new panel member.
     * Used by HR to add panel users.
     *
     * @param request panel request data
     * @return created panel response
     */
    @PostMapping
    public ResponseEntity<PanelResponseDTO> createPanel(
            @Valid @RequestBody PanelRequestDTO request) {

        PanelResponseDTO response = panelService.createPanel(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Returns all panel members.
     *
     * @return list of panel responses
     */
    @GetMapping
    public ResponseEntity<List<PanelResponseDTO>> getAllPanels() {
        return ResponseEntity.ok(panelService.getAllPanels());
    }

    /**
     * Returns panel details by id.
     *
     * @param id panel id
     * @return panel response
     */
    @GetMapping(HrApiConstants.PANELS_BY_ID)
    public ResponseEntity<PanelResponseDTO> getPanelById(
            @PathVariable Long id) {

        return ResponseEntity.ok(panelService.getPanelById(id));
    }

    /**
     * Activates a panel member.
     * Also creates login credentials for the panel user.
     *
     * @param id panel id
     * @return updated panel response
     */
    @PutMapping(HrApiConstants.PANELS_ACTIVATE)
    public ResponseEntity<PanelResponseDTO> activatePanel(
            @PathVariable Long id) {

        return ResponseEntity.ok(panelService.activatePanel(id));
    }
}