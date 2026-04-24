package com.capstone.interviewtracker.service.impl;

import com.capstone.interviewtracker.dto.Request.LoginRequest;
import com.capstone.interviewtracker.dto.Request.SignupRequest;
import com.capstone.interviewtracker.dto.Response.AuthResponse;
import com.capstone.interviewtracker.exception.InvalidCredentialsException;
import com.capstone.interviewtracker.exception.ResourceNotFoundException;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.UserRepository;
import com.capstone.interviewtracker.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * This class contains the logic for signup and login.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor to initialize repository and password encoder.
     */
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user in the system.
     */
    @Override
    public AuthResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                "Signup successful");
    }

    /**
     * Checks user login details.
     */
    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                "Login successful");
    }
}