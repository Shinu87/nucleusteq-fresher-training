package com.capstone.interviewtracker.controller;

import com.capstone.interviewtracker.constants.api.AuthApiConstants;
import com.capstone.interviewtracker.dto.Request.LoginRequest;
import com.capstone.interviewtracker.dto.Request.SetPasswordRequest;
import com.capstone.interviewtracker.dto.Request.SignupRequest;
import com.capstone.interviewtracker.dto.Response.AuthResponse;
import com.capstone.interviewtracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This controller handles authentication APIs like signup and login.
 */
@RestController
@RequestMapping(AuthApiConstants.BASE_PATH)
public class AuthController {

    private final UserService userService;

    /**
     * Constructor for AuthController.
     *
     * @param userService service for user authentication logic
     */
    public AuthController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * API to register new user.
     *
     * @param request the signup request
     * @return authentication response
     */
    @PostMapping(AuthApiConstants.SIGNUP)
    public ResponseEntity<AuthResponse> signup(
            @Valid @RequestBody final SignupRequest request) {

        return ResponseEntity.ok(userService.signup(request));
    }

    /**
     * API to login user.
     *
     * @param request the login request
     * @return authentication response
     */
    @PostMapping(AuthApiConstants.LOGIN)
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody final LoginRequest request) {

        return ResponseEntity.ok(userService.login(request));
    }

    /**
     * Set password using one-time token sent to email.
     *
     * @param request set password request
     * @return authentication response
     */
    @PostMapping(AuthApiConstants.SET_PASSWORD)
    public ResponseEntity<AuthResponse> setPassword(
            @Valid @RequestBody final SetPasswordRequest request) {

        return ResponseEntity.ok(userService.setPassword(request));
    }
}