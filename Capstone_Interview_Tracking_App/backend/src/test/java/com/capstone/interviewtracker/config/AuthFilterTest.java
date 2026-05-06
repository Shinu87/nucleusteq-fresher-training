package com.capstone.interviewtracker.config;

import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for AuthFilter.
 */
class AuthFilterTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthFilter authFilter;

    /**
     * Sets up the filter and mocks before each test.
     */
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authFilter = new AuthFilter(userRepository, passwordEncoder);
        SecurityContextHolder.clearContext();
    }

    /**
     * Tests that no authentication is set when Authorization header is missing.
     */
    @Test
    void testNoAuthHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        authFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Tests that a non-Basic header does not set authentication.
     */
    @Test
    void testInvalidHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer token");

        authFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Tests that valid Basic Auth credentials set the security context.
     */
    @Test
    void testValidBasicAuthSetsSecurityContext() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encoded");
        user.setRole(Role.HR);
        user.setEnabled(true);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String raw = "test@test.com:password";
        String encoded = Base64.getEncoder().encodeToString(raw.getBytes());

        when(request.getHeader("Authorization"))
                .thenReturn("Basic " + encoded);

        authFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Tests that a wrong password does not authenticate the user.
     */
    @Test
    void testWrongPasswordDoesNotAuthenticate() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encoded");
        user.setRole(Role.HR);
        user.setEnabled(true);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        String raw = "test@test.com:wrongpassword";
        String encoded = Base64.getEncoder().encodeToString(raw.getBytes());

        when(request.getHeader("Authorization"))
                .thenReturn("Basic " + encoded);

        authFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Tests that a disabled user is not authenticated even with correct password.
     */
    @Test
    void testDisabledUserDoesNotAuthenticate() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encoded");
        user.setRole(Role.HR);
        user.setEnabled(false);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        String raw = "test@test.com:password";
        String encoded = Base64.getEncoder().encodeToString(raw.getBytes());

        when(request.getHeader("Authorization"))
                .thenReturn("Basic " + encoded);

        authFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    /**
     * Tests that invalid base64 in the header does not throw and clears context.
     */
    @Test
    void testExceptionClearsContext() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Basic invalid_base64");

        FilterChain chain = mock(FilterChain.class);

        authFilter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
