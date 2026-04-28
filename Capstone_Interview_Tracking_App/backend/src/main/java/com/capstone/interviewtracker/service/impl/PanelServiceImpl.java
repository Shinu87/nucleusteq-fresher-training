package com.capstone.interviewtracker.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.capstone.interviewtracker.constants.PanelConstants;
import com.capstone.interviewtracker.dto.Request.PanelRequestDTO;
import com.capstone.interviewtracker.dto.Response.PanelResponseDTO;
import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.exception.ResourceAlreadyExistsException;
import com.capstone.interviewtracker.exception.ResourceNotFoundException;
import com.capstone.interviewtracker.model.Panel;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.PanelRepository;
import com.capstone.interviewtracker.repository.UserRepository;
import com.capstone.interviewtracker.service.EmailService;
import com.capstone.interviewtracker.service.PanelService;

/**
 * Service for panel member operations.
 */
@Service
public class PanelServiceImpl implements PanelService {

    private final PanelRepository panelRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserServiceImpl userServiceImpl;

    public PanelServiceImpl(PanelRepository panelRepository,
            UserRepository userRepository,
            EmailService emailService,
            UserServiceImpl userServiceImpl) {
        this.panelRepository = panelRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userServiceImpl = userServiceImpl;
    }

    /**
     * HR creates panel member.
     * Account remains inactive until activation.
     */
    @Override
    public PanelResponseDTO createPanel(PanelRequestDTO request) {

        if (panelRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException(PanelConstants.EMAIL_EXISTS);
        }

        if (panelRepository.existsByMobile(request.getMobile())) {
            throw new ResourceAlreadyExistsException(PanelConstants.MOBILE_EXISTS);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException(PanelConstants.USER_EXISTS);
        }

        Panel panel = new Panel();
        panel.setName(request.getName().trim());
        panel.setEmail(request.getEmail().trim().toLowerCase());
        panel.setMobile(request.getMobile().trim());
        panel.setOrganization(request.getOrganization().trim());
        panel.setDesignation(request.getDesignation().trim());
        panel.setActive(false);

        Panel saved = panelRepository.save(panel);
        return mapToResponse(saved);
    }

    @Override
    public List<PanelResponseDTO> getAllPanels() {
        return panelRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public PanelResponseDTO getPanelById(Long id) {
        Panel panel = panelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Panel member not found with id: " + id));
        return mapToResponse(panel);
    }

    /**
     * HR activates panel.
     */
    @Override
    public PanelResponseDTO activatePanel(Long panelId) {

        Panel panel = panelRepository.findById(panelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Panel member not found with id: " + panelId));

        if (panel.isActive()) {
            throw new RuntimeException("Panel member is already active");
        }

        User user = userRepository.findByEmail(panel.getEmail()).orElse(null);

        if (user == null) {
            user = new User();
            user.setName(panel.getName());
            user.setEmail(panel.getEmail());
            user.setPassword(null);
            user.setRole(Role.PANEL);
            user.setEnabled(false);
            user = userRepository.save(user);
        } else {
            user.setPassword(null);
            user.setEnabled(false);
            user.setRole(Role.PANEL);
            user.setName(panel.getName());
            user = userRepository.save(user);
        }

        /**
         * link user account to panel and mark panel as active
         */
        panel.setUser(user);
        panel.setActive(true);
        Panel saved = panelRepository.save(panel);

        /**
         * generate setup token, build link and send email to panel
         */
        String setLink = userServiceImpl.createTokenAndBuildLink(
                panel.getEmail(), Role.PANEL.name());
        emailService.sendPanelActivationEmail(
                panel.getEmail(), panel.getName(), setLink);

        return mapToResponse(saved);
    }

    private PanelResponseDTO mapToResponse(Panel panel) {
        PanelResponseDTO response = new PanelResponseDTO();
        response.setId(panel.getId());
        response.setName(panel.getName());
        response.setEmail(panel.getEmail());
        response.setMobile(panel.getMobile());
        response.setOrganization(panel.getOrganization());
        response.setDesignation(panel.getDesignation());
        response.setActive(panel.isActive());
        return response;
    }
}