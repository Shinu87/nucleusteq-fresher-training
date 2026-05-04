package com.capstone.interviewtracker.service;

import com.capstone.interviewtracker.dto.Request.LoginRequest;
import com.capstone.interviewtracker.dto.Request.SetPasswordRequest;
import com.capstone.interviewtracker.dto.Request.SignupRequest;
import com.capstone.interviewtracker.dto.Response.AuthResponse;

/**
 * Service interface for user authentication and account management.
 * Handles signup, login, password setup, and token generation.
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     *
     * @param request signup request containing user details
     * @return authentication response after successful registration
     */
    AuthResponse signup(SignupRequest request);

    /**
     * Authenticates a user and allows login.
     *
     * @param request login request containing email and password
     * @return authentication response after successful login
     */
    AuthResponse login(LoginRequest request);

    /**
     * Sets password for a user using a valid setup token.
     *
     * @param request password setup request containing token and new password
     * @return authentication response after password is set
     */
    AuthResponse setPassword(SetPasswordRequest request);

    /**
     * Creates a single-use password setup token and returns the frontend link.
     * This link is sent via email for setting the account password.
     *
     * @param email user email
     * @param role  role of the user (CANDIDATE or PANEL)
     * @return frontend URL containing the generated token
     */
    String createTokenAndBuildLink(String email, String role);
}