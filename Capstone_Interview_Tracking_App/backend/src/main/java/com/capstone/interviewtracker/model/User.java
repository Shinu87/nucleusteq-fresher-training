package com.capstone.interviewtracker.model;

import com.capstone.interviewtracker.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// This class represents the users table in database
@Entity
@Table(name = "users")
public class User {

    // Primary key for user table - auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name should not be empty
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    // Email should be valid and unique for each user
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    // Password field - should not be empty
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    // Role of user
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Default Constructor
    public User() {
    }

    // Constructor to create user object without id
    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getter for ID
    public Long getId() {
        return id;
    }

    // Getter and Setter for Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for Password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and Setter for Role
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}