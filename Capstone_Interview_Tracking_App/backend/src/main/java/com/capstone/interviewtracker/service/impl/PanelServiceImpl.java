package com.capstone.interviewtracker.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.capstone.interviewtracker.constants.messages.PanelMessages;
import com.capstone.interviewtracker.dto.Request.PanelRequestDTO;
import com.capstone.interviewtracker.dto.Response.PanelResponseDTO;
import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.exception.custom.ConflictException;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
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

    /**
     * Constructor injection for PanelService dependencies.
     *
     * @param panelRepository repository for panel data
     * @param userRepository  repository for user data
     * @param emailService    service for sending emails
     * @param userServiceImpl service for user-related operations
     */
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
     * Creates a new panel member by HR.
     * Ensures email, mobile, and user email are unique before creation.
     * The panel account is created in inactive state and must be activated later.
     *
     * @param request panel creation request containing personal and work details
     * @return created panel response DTO
     */
    @Override
    public PanelResponseDTO createPanel(PanelRequestDTO request) {

        if (panelRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(
                    PanelMessages.EMAIL_EXISTS);
        }

        if (panelRepository.existsByMobile(request.getMobile())) {
            throw new ConflictException(
                    PanelMessages.MOBILE_EXISTS);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(
                    PanelMessages.USER_EXISTS);
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

    /**
     * Retrieves all panel members from the system.
     *
     * @return list of all panel response DTOs
     */
    @Override
    public List<PanelResponseDTO> getAllPanels() {
        return panelRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Retrieves a panel member by their unique ID.
     *
     * @param id panel member ID
     * @return panel response DTO
     * @throws ResourceNotFoundException if panel is not found
     */
    @Override
    public PanelResponseDTO getPanelById(Long id) {
        Panel panel = panelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PanelMessages.NOT_FOUND + id));

        return mapToResponse(panel);
    }

    /**
     * Activates a panel member after HR approval.
     * Creates or updates the associated user account, links it to the panel,
     * and sends a password setup email.
     *
     * @param panelId panel member ID to activate
     * @return activated panel response DTO
     * @throws ResourceNotFoundException if panel is not found
     */
    @Override
    public PanelResponseDTO activatePanel(Long panelId) {

        Panel panel = panelRepository.findById(panelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PanelMessages.NOT_FOUND + panelId));

        if (panel.isActive()) {
            throw new ConflictException(
                    PanelMessages.ALREADY_ACTIVE);
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

        panel.setUser(user);
        panel.setActive(true);
        Panel saved = panelRepository.save(panel);

        String setLink = userServiceImpl.createTokenAndBuildLink(
                panel.getEmail(), Role.PANEL.name());

        emailService.sendPanelActivationEmail(
                panel.getEmail(), panel.getName(), setLink);

        return mapToResponse(saved);
    }

    /**
     * Maps a Panel entity to its response DTO.
     *
     * @param panel panel entity
     * @return panel response DTO
     */
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