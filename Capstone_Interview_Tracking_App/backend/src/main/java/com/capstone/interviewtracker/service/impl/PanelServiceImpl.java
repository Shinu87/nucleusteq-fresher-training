package com.capstone.interviewtracker.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import com.capstone.interviewtracker.constants.messages.PanelMessages;
import com.capstone.interviewtracker.dto.Request.PanelRequestDTO;
import com.capstone.interviewtracker.dto.Response.PanelResponseDTO;
import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.exception.custom.BadRequestException;
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

    // logger for this service class
    private static final Logger logger = LoggerFactory.getLogger(PanelServiceImpl.class);

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

        // logging when create panel request is received
        logger.info("Create panel request received for email: {}", request.getEmail());

        // logging before checking if panel email already exists in DB
        logger.debug("Checking if panel email already exists: {}", request.getEmail());
        if (panelRepository.existsByEmail(request.getEmail())) {
            // logging when panel email is already registered
            logger.warn("Create panel failed - email already exists: {}", request.getEmail());
            throw new ConflictException(
                    PanelMessages.EMAIL_EXISTS);
        }

        // logging before checking if panel mobile already exists in DB
        logger.debug("Checking if panel mobile already exists: {}", request.getMobile());
        if (panelRepository.existsByMobile(request.getMobile())) {
            // logging when panel mobile number is already registered
            logger.warn("Create panel failed - mobile already exists: {}", request.getMobile());
            throw new ConflictException(
                    PanelMessages.MOBILE_EXISTS);
        }

        // logging before checking if user with same email already exists
        logger.debug("Checking if user account already exists for email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            // logging when a user account already exists for this email
            logger.warn("Create panel failed - user account already exists for email: {}", request.getEmail());
            throw new ConflictException(
                    PanelMessages.USER_EXISTS);
        }

        // logging before building and saving the panel object
        logger.debug("Building new Panel object for email: {}", request.getEmail());
        Panel panel = new Panel();
        panel.setName(request.getName().trim());
        panel.setEmail(request.getEmail().trim().toLowerCase());
        panel.setMobile(request.getMobile().trim());
        panel.setOrganization(request.getOrganization().trim());
        panel.setDesignation(request.getDesignation().trim());
        panel.setExpertise(request.getExpertise().trim());
        panel.setActive(false);

        // logging before saving panel to DB
        logger.debug("Saving new panel to database for email: {}", panel.getEmail());
        Panel saved = panelRepository.save(panel);
        // logging after panel is saved successfully
        logger.info("Panel created successfully with ID: {} for email: {}", saved.getId(), saved.getEmail());

        return mapToResponse(saved);
    }

    /**
     * Retrieves all panel members from the system.
     *
     * @return list of all panel response DTOs
     */
    @Override
    public List<PanelResponseDTO> getAllPanels() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Panel> panels;

        if (user.getRole() == Role.HR) {

            logger.info("HR → fetching all panels");
            panels = panelRepository.findAll();

        } else if (user.getRole() == Role.PANEL) {

            logger.info("PANEL → fetching only own panel");

            Panel panel = panelRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Panel not found"));

            panels = List.of(panel);

        } else {
            throw new BadRequestException("Access denied");
        }

        return panels.stream()
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

        // logging when get panel by ID request is received
        logger.info("Fetching panel member for ID: {}", id);

        // logging before querying DB for panel by ID
        logger.debug("Looking up panel in database for ID: {}", id);
        Panel panel = panelRepository.findById(id)
                .orElseThrow(() -> {
                    // logging when panel is not found for given ID
                    logger.warn("Panel not found for ID: {}", id);
                    return new ResourceNotFoundException(PanelMessages.NOT_FOUND + id);
                });

        // logging after panel is found successfully
        logger.debug("Panel found for ID: {}, email: {}", id, panel.getEmail());

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

        // logging when activate panel request is received
        logger.info("Activate panel request received for panelId: {}", panelId);

        // logging before looking up panel in DB
        logger.debug("Looking up panel in database for panelId: {}", panelId);
        Panel panel = panelRepository.findById(panelId)
                .orElseThrow(() -> {
                    // logging when panel is not found for activation
                    logger.warn("Activate panel failed - panel not found for ID: {}", panelId);
                    return new ResourceNotFoundException(PanelMessages.NOT_FOUND + panelId);
                });

        if (panel.isActive()) {
            // logging when panel is already active
            logger.warn("Activate panel failed - panel is already active for ID: {}", panelId);
            throw new ConflictException(
                    PanelMessages.ALREADY_ACTIVE);
        }

        // logging before checking if a user account exists for this panel's email
        logger.debug("Checking if user account exists for panel email: {}", panel.getEmail());
        User user = userRepository.findByEmail(panel.getEmail()).orElse(null);

        if (user == null) {
            // logging when no existing user found - creating new user account
            logger.info("No existing user found for email: {} - creating new user account", panel.getEmail());
            user = new User();
            user.setName(panel.getName());
            user.setEmail(panel.getEmail());
            user.setPassword(null);
            user.setRole(Role.PANEL);
            user.setEnabled(false);

            // logging before saving new user to DB
            logger.debug("Saving new user account for panel email: {}", panel.getEmail());
            user = userRepository.save(user);
            // logging after new user is saved
            logger.info("New user account created with ID: {} for panel email: {}", user.getId(), user.getEmail());
        } else {
            // logging when existing user found - updating user account for panel role
            logger.info("Existing user found for email: {} - updating user account for PANEL role", panel.getEmail());
            user.setPassword(null);
            user.setEnabled(false);
            user.setRole(Role.PANEL);
            user.setName(panel.getName());

            // logging before saving updated user to DB
            logger.debug("Saving updated user account for panel email: {}", panel.getEmail());
            user = userRepository.save(user);
            // logging after existing user is updated
            logger.info("Existing user account updated with ID: {} for panel email: {}", user.getId(), user.getEmail());
        }

        // logging before linking user to panel and marking panel as active
        logger.debug("Linking user to panel and setting panel active for panelId: {}", panelId);
        panel.setUser(user);
        panel.setActive(true);

        // logging before saving activated panel to DB
        logger.debug("Saving activated panel to database for panelId: {}", panelId);
        Panel saved = panelRepository.save(panel);
        // logging after panel is saved as active
        logger.info("Panel activated and saved successfully for panelId: {}", panelId);

        // logging before generating password setup token and link for panel
        logger.debug("Generating password setup token for panel email: {}", panel.getEmail());
        String setLink = userServiceImpl.createTokenAndBuildLink(
                panel.getEmail(), Role.PANEL.name());

        // logging before sending activation email to panel member
        logger.info("Sending activation email to panel member: {}", panel.getEmail());
        emailService.sendPanelActivationEmail(
                panel.getEmail(), panel.getName(), setLink);
        // logging after activation email is sent
        logger.info("Activation email sent successfully to panel member: {}", panel.getEmail());

        return mapToResponse(saved);
    }

    /**
     * Maps a Panel entity to its response DTO.
     *
     * @param panel panel entity
     * @return panel response DTO
     */
    private PanelResponseDTO mapToResponse(Panel panel) {
        // logging when panel entity is being mapped to response DTO
        logger.debug("Mapping panel entity to response DTO for panelId: {}", panel.getId());
        PanelResponseDTO response = new PanelResponseDTO();
        response.setId(panel.getId());
        response.setName(panel.getName());
        response.setEmail(panel.getEmail());
        response.setMobile(panel.getMobile());
        response.setOrganization(panel.getOrganization());
        response.setDesignation(panel.getDesignation());
        response.setExpertise(panel.getExpertise());
        response.setActive(panel.isActive());
        return response;
    }
}