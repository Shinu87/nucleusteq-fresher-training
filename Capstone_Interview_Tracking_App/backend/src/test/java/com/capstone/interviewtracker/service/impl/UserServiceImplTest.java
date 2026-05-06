package com.capstone.interviewtracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import com.capstone.interviewtracker.dto.Request.LoginRequest;
import com.capstone.interviewtracker.dto.Request.SetPasswordRequest;
import com.capstone.interviewtracker.dto.Request.SignupRequest;
import com.capstone.interviewtracker.dto.Response.AuthResponse;
import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.exception.custom.BadRequestException;
import com.capstone.interviewtracker.exception.custom.ConflictException;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
import com.capstone.interviewtracker.exception.custom.UnauthorizedException;
import com.capstone.interviewtracker.model.PasswordSetupToken;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.PasswordSetupTokenRepository;
import com.capstone.interviewtracker.repository.UserRepository;
import com.capstone.interviewtracker.service.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Test class for UserServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordSetupTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private SetPasswordRequest setPasswordRequest;
    private User savedUser;

    private static final String RAW_PASSWORD = "Secret@123";

    private static final String BASE64_PASSWORD = Base64.getEncoder()
            .encodeToString(RAW_PASSWORD.getBytes(StandardCharsets.UTF_8));

    /**
     * Sets up common test data before each test.
     */
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "frontendBaseUrl", "http://localhost:3000");

        signupRequest = new SignupRequest();
        signupRequest.setName("Aliya");
        signupRequest.setEmail("Aliya@Example.com");
        signupRequest.setMobile("9876543210");
        signupRequest.setGender("FEMALE");
        signupRequest.setDateOfBirth(LocalDate.of(1997, 6, 15));
        signupRequest.setRole(Role.CANDIDATE);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("Aliya@example.com");
        loginRequest.setPassword(BASE64_PASSWORD);

        setPasswordRequest = new SetPasswordRequest();
        setPasswordRequest.setToken("token123");
        setPasswordRequest.setPassword("NewSecret@123");

        savedUser = new User();
        ReflectionTestUtils.setField(savedUser, "id", 7L);
        savedUser.setEmail("Aliya@example.com");
        savedUser.setName("Aliya");
        savedUser.setRole(Role.CANDIDATE);
        savedUser.setDateOfBirth(LocalDate.of(1997, 6, 15));
        savedUser.setEnabled(true);
    }

    /**
     * Tests successful signup of a new user.
     */
    @Test
    void testSignupSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            ReflectionTestUtils.setField(u, "id", 7L);
            return u;
        });
        when(tokenRepository.save(any(PasswordSetupToken.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(emailService).sendCandidateSignupEmail(anyString(), anyString(), anyString());

        AuthResponse response = userService.signup(signupRequest);

        assertNotNull(response);
        assertEquals("aliya@example.com", response.getEmail());
        verify(emailService).sendCandidateSignupEmail(anyString(), anyString(), anyString());
    }

    /**
     * Tests that signing up with a duplicate email throws an exception.
     */
    @Test
    void testSignupDuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(ConflictException.class, () -> userService.signup(signupRequest));
    }

    /**
     * Tests that signup with null role defaults to CANDIDATE.
     */
    @Test
    void testSignupNullRoleDefaultsToCandidate() {
        signupRequest.setRole(null);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            ReflectionTestUtils.setField(u, "id", 7L);
            return u;
        });
        when(tokenRepository.save(any(PasswordSetupToken.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(emailService).sendCandidateSignupEmail(anyString(), anyString(), anyString());

        AuthResponse response = userService.signup(signupRequest);
        assertEquals(Role.CANDIDATE, response.getRole());
    }

    /**
     * Tests successful login with correct credentials.
     */
    @Test
    void testLoginSuccess() {
        savedUser.setPassword("hashed");
        when(userRepository.findByEmail("Aliya@example.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches(RAW_PASSWORD, "hashed")).thenReturn(true);

        AuthResponse response = userService.login(loginRequest);

        assertEquals("Login successful", response.getMessage());
    }

    /**
     * Tests login when user is not found.
     */
    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.login(loginRequest));
    }

    /**
     * Tests login when user has not set a password yet.
     */
    @Test
    void testLoginPasswordNotSet() {
        savedUser.setPassword(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(savedUser));
        assertThrows(BadRequestException.class, () -> userService.login(loginRequest));
    }

    /**
     * Tests login when stored password is blank.
     */
    @Test
    void testLoginBlankPassword() {
        savedUser.setPassword("   ");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(savedUser));
        assertThrows(BadRequestException.class, () -> userService.login(loginRequest));
    }

    /**
     * Tests login fails when wrong password is provided.
     */
    @Test
    void testLoginWrongPassword() {
        savedUser.setPassword("hashed");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches(RAW_PASSWORD, "hashed")).thenReturn(false);
        assertThrows(BadRequestException.class, () -> userService.login(loginRequest));
    }

    /**
     * Tests that disabled accounts cannot log in.
     */
    @Test
    void testLoginDisabledAccount() {
        savedUser.setPassword("hashed");
        savedUser.setEnabled(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches(RAW_PASSWORD, "hashed")).thenReturn(true);

        assertThrows(UnauthorizedException.class, () -> userService.login(loginRequest));
    }

    /**
     * Tests successful password setup and that user gets enabled.
     */
    @Test
    void testSetPasswordSuccess() {
        PasswordSetupToken token = new PasswordSetupToken(
                "token123", "Aliya@example.com", "CANDIDATE",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail("Aliya@example.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokenRepository.save(any(PasswordSetupToken.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse response = userService.setPassword(setPasswordRequest);
        assertNotNull(response);
        assertTrue(savedUser.isEnabled());
    }

    /**
     * Tests that an invalid token throws an exception.
     */
    @Test
    void testSetPasswordInvalidToken() {
        when(tokenRepository.findByToken("token123")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.setPassword(setPasswordRequest));
    }

    /**
     * Tests that an already-used token cannot be reused.
     */
    @Test
    void testSetPasswordUsedToken() {
        PasswordSetupToken token = new PasswordSetupToken(
                "token123", "Aliya@example.com", "CANDIDATE",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        token.setUsed(true);
        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));

        assertThrows(BadRequestException.class, () -> userService.setPassword(setPasswordRequest));
    }

    /**
     * Tests that an expired token cannot be used.
     */
    @Test
    void testSetPasswordExpiredToken() {
        PasswordSetupToken token = new PasswordSetupToken(
                "token123", "Aliya@example.com", "CANDIDATE",
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));

        assertThrows(BadRequestException.class, () -> userService.setPassword(setPasswordRequest));
    }

    /**
     * Tests setPassword when no user is found for the token's email.
     */
    @Test
    void testSetPasswordNoUserForToken() {
        PasswordSetupToken token = new PasswordSetupToken(
                "token123", "ghost@example.com", "CANDIDATE",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.setPassword(setPasswordRequest));
    }

    /**
     * Tests that createTokenAndBuildLink returns a frontend URL containing the
     * token.
     */
    @Test
    void testCreateTokenAndBuildLink() {
        when(tokenRepository.save(any(PasswordSetupToken.class))).thenAnswer(inv -> inv.getArgument(0));

        String link = userService.createTokenAndBuildLink("user@example.com", "CANDIDATE");

        assertTrue(link.startsWith("http://localhost:3000/set-password.html?token="));
        assertTrue(link.length() > "http://localhost:3000/set-password.html?token=".length());
    }
}
