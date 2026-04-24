package com.capstone.interviewtracker.model;

import com.capstone.interviewtracker.enums.Role;
import jakarta.persistence.*;

/**
 * Represents application users for authentication and authorization.
 * Users can be HR, PANEL, or CANDIDATE based on role.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Primary key for user table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of user.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Email of user.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Encrypted password.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Role of the user (HR / PANEL / CANDIDATE).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Constructors

    public User() {
    }

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}