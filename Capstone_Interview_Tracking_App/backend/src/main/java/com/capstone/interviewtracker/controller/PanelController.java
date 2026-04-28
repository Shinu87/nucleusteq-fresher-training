package com.capstone.interviewtracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.interviewtracker.dto.Request.PanelRequestDTO;
import com.capstone.interviewtracker.dto.Response.PanelResponseDTO;
import com.capstone.interviewtracker.service.PanelService;

import jakarta.validation.Valid;

/**
 * PanelController handles all API requests related to panel members
 * like create, get, and activate panel users.
 */
@RestController
@RequestMapping("/api/panels")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class PanelController {

    private final PanelService panelService;

    /** constructor injection for panel service */
    public PanelController(PanelService panelService) {
        this.panelService = panelService;
    }

    /**
     * API to create a new panel member (HR side)
     */
    @PostMapping
    public ResponseEntity<PanelResponseDTO> createPanel(@Valid @RequestBody PanelRequestDTO request) {
        PanelResponseDTO response = panelService.createPanel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * API to get all panel members
     */
    @GetMapping
    public List<PanelResponseDTO> getAllPanels() {
        return panelService.getAllPanels();
    }

    /**
     * API to get panel details by id
     */
    @GetMapping("/{id}")
    public PanelResponseDTO getPanelById(@PathVariable Long id) {
        return panelService.getPanelById(id);
    }

    /**
     * API to activate a panel member
     * and create their login account
     */
    @PutMapping("/{id}/activate")
    public PanelResponseDTO activatePanel(@PathVariable Long id) {
        return panelService.activatePanel(id);
    }
}