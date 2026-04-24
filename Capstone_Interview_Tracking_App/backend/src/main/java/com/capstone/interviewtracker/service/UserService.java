package com.capstone.interviewtracker.service;

import com.capstone.interviewtracker.dto.Request.LoginRequest;
import com.capstone.interviewtracker.dto.Request.SignupRequest;
import com.capstone.interviewtracker.dto.Response.AuthResponse;

/**
 * This interface defines methods for user signup and login.
 */
public interface UserService {

    /**
     * Used to register a new user.
     */
    AuthResponse signup(SignupRequest request);

    /**
     * Used to login user.
     */
    AuthResponse login(LoginRequest request);

}
