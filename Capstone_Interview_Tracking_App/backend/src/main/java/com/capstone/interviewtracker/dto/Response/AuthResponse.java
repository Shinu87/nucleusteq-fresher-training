package com.capstone.interviewtracker.dto.Response;

import com.capstone.interviewtracker.enums.Role;

/**
 * This class is used to send response after login or signup.
 */
public class AuthResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String message;

    /**
     * Constructor to set user details and message.
     */
    public AuthResponse(Long id, String name, String email, Role role, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.message = message;
    }

    /**
     * Returns user id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns user name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns user role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Returns message.
     */
    public String getMessage() {
        return message;
    }
}