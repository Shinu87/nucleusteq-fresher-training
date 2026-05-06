package com.capstone.interviewtracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.capstone.interviewtracker.dto.Request.PanelRequestDTO;
import com.capstone.interviewtracker.dto.Response.PanelResponseDTO;
import com.capstone.interviewtracker.exception.custom.ConflictException;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
import com.capstone.interviewtracker.model.Panel;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.PanelRepository;
import com.capstone.interviewtracker.repository.UserRepository;
import com.capstone.interviewtracker.service.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Test class for PanelServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class PanelServiceImplTest {

    @Mock
    private PanelRepository panelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UserServiceImpl userServiceImpl;

    @InjectMocks
    private PanelServiceImpl panelService;

    private PanelRequestDTO request;
    private Panel panel;

    /**
     * Sets up common test data before each test.
     */
    @BeforeEach
    void setUp() {
        request = new PanelRequestDTO();
        request.setName("  Salman  ");
        request.setEmail("Salman@example.com");
        request.setMobile("9876543210");
        request.setOrganization("Acme Corp");
        request.setDesignation("Senior Engineer");
        request.setExpertise("Java, Spring Boot");

        panel = new Panel();
        panel.setId(5L);
        panel.setName("Salman");
        panel.setEmail("Salman@example.com");
        panel.setMobile("9876543210");
        panel.setOrganization("Acme Corp");
        panel.setDesignation("Senior Engineer");
        panel.setActive(false);
    }

    /**
     * Tests creating a panel and ensures input fields are normalized.
     */
    @Test
    void testCreatePanelSuccess() {
        when(panelRepository.existsByEmail(anyString())).thenReturn(false);
        when(panelRepository.existsByMobile(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(panelRepository.save(any(Panel.class))).thenAnswer(invocation -> {
            Panel p = invocation.getArgument(0);
            p.setId(5L);
            return p;
        });

        PanelResponseDTO result = panelService.createPanel(request);

        assertEquals("Salman", result.getName());
        assertEquals("salman@example.com", result.getEmail());
    }

    /**
     * Tests that creating a panel with a duplicate email throws an exception.
     */
    @Test
    void testCreatePanelDuplicateEmail() {
        when(panelRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(ConflictException.class, () -> panelService.createPanel(request));
    }

    /**
     * Tests that creating a panel with a duplicate mobile throws an exception.
     */
    @Test
    void testCreatePanelDuplicateMobile() {
        when(panelRepository.existsByEmail(anyString())).thenReturn(false);
        when(panelRepository.existsByMobile(anyString())).thenReturn(true);
        assertThrows(ConflictException.class, () -> panelService.createPanel(request));
    }

    /**
     * Tests that creating a panel fails when an existing user has the same email.
     */
    @Test
    void testCreatePanelDuplicateUserEmail() {
        when(panelRepository.existsByEmail(anyString())).thenReturn(false);
        when(panelRepository.existsByMobile(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(ConflictException.class, () -> panelService.createPanel(request));
    }

    /**
     * Tests fetching all panels returns the mapped list.
     */
    @Test
    void testGetAllPanels() {
        when(panelRepository.findAll()).thenReturn(List.of(panel));
        List<PanelResponseDTO> result = panelService.getAllPanels();
        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getId());
    }

    /**
     * Tests fetching a panel by id successfully.
     */
    @Test
    void testGetPanelById() {
        when(panelRepository.findById(5L)).thenReturn(Optional.of(panel));
        PanelResponseDTO result = panelService.getPanelById(5L);
        assertEquals("Salman", result.getName());
    }

    /**
     * Tests fetching a panel by id when it does not exist.
     */
    @Test
    void testGetPanelByIdNotFound() {
        when(panelRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> panelService.getPanelById(999L));
    }

    /**
     * Tests activating a panel and creating a new user when none exists.
     */
    @Test
    void testActivatePanelCreatesNewUser() {
        when(panelRepository.findById(5L)).thenReturn(Optional.of(panel));
        when(userRepository.findByEmail("Salman@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            ReflectionTestUtils.setField(u, "id", 50L);
            return u;
        });
        when(panelRepository.save(any(Panel.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userServiceImpl.createTokenAndBuildLink(anyString(), anyString())).thenReturn("http://link");
        doNothing().when(emailService).sendPanelActivationEmail(anyString(), anyString(), anyString());

        PanelResponseDTO result = panelService.activatePanel(5L);

        assertTrue(result.isActive());
        verify(emailService).sendPanelActivationEmail(anyString(), anyString(), anyString());
    }

    /**
     * Tests activating a panel reuses the existing user when present.
     */
    @Test
    void testActivatePanelReusesExistingUser() {
        User existing = new User();
        ReflectionTestUtils.setField(existing, "id", 60L);
        existing.setEmail("Salman@example.com");
        existing.setName("Salman");
        when(panelRepository.findById(5L)).thenReturn(Optional.of(panel));
        when(userRepository.findByEmail("Salman@example.com")).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(panelRepository.save(any(Panel.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userServiceImpl.createTokenAndBuildLink(anyString(), anyString())).thenReturn("http://link");
        doNothing().when(emailService).sendPanelActivationEmail(anyString(), anyString(), anyString());

        PanelResponseDTO result = panelService.activatePanel(5L);

        assertTrue(result.isActive());
    }

    /**
     * Tests that activating an already active panel throws an exception.
     */
    @Test
    void testActivatePanelAlreadyActive() {
        panel.setActive(true);
        when(panelRepository.findById(5L)).thenReturn(Optional.of(panel));
        assertThrows(RuntimeException.class, () -> panelService.activatePanel(5L));
    }

    /**
     * Tests activating a panel that does not exist.
     */
    @Test
    void testActivatePanelNotFound() {
        when(panelRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> panelService.activatePanel(999L));
    }
}