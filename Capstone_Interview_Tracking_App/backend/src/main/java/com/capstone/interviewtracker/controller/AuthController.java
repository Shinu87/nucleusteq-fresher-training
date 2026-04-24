package com.capstone.interviewtracker.controller;

import com.capstone.interviewtracker.dto.Request.LoginRequest;
import com.capstone.interviewtracker.dto.Request.SignupRequest;
import com.capstone.interviewtracker.dto.Response.AuthResponse;
import com.capstone.interviewtracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * This controller handles authentication APIs like signup and login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * API to register new user.
     */
    @PostMapping("/signup")
    public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
        return userService.signup(request);
    }

    /**
     * API to login user.
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }
}