package com.capstone.interviewtracker.dto.Response;

import com.capstone.interviewtracker.enums.Role;
import java.time.LocalDate;

/**
 * This class is used to send response after login or signup.
 */
public class AuthResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String message;
    private LocalDate dateOfBirth;

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

    public AuthResponse(Long id, String name, String email, Role role, String message, LocalDate dateOfBirth) {
        this(id, name, email, role, message);
        this.dateOfBirth = dateOfBirth;
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

    /**
     * Returns candidate date of birth.
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Derives age from dateOfBirth.
     */
    public Integer getAge() {
        if (dateOfBirth == null)
            return null;
        return java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}