package com.capstone.interviewtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// This class represents panel members in the system
@Entity
@Table(name = "panels")
public class Panel {

    // Primary key for panel table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name of the panel member
    @NotBlank
    @Column(nullable = false)
    private String name;

    // Email should be valid and unique
    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    // Mobile number should be unique
    @NotBlank
    @Column(nullable = false, unique = true)
    private String mobile;

    // Organization where panel member works
    @NotBlank
    @Column(nullable = false)
    private String organization;

    // Designation of the panel member
    @NotBlank
    @Column(nullable = false)
    private String designation;

    // To check if panel is active or not
    @Column(nullable = false)
    private boolean active = false;

    // Link panel to user account for login
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // default constructor
    public Panel() {
    }

    // constructor
    public Panel(String name, String email, String mobile,
            String organization, String designation,
            boolean active) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.organization = organization;
        this.designation = designation;
        this.active = active;
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
