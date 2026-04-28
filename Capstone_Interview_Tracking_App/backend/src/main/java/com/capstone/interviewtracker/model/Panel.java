package com.capstone.interviewtracker.model;

import jakarta.persistence.*;

/**
 * Represents panel members who conduct interviews in the system.
 */
@Entity
@Table(name = "panels")
public class Panel {

    /**
     * Primary key for panel table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the panel member.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Email of panel member.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Mobile number of panel member.
     */
    @Column(nullable = false, unique = true)
    private String mobile;

    /**
     * Organization of panel member.
     */
    @Column(nullable = false)
    private String organization;

    /**
     * Designation of panel member.
     */
    @Column(nullable = false)
    private String designation;

    /**
     * Indicates whether panel is active or not.
     */
    @Column(nullable = false)
    private boolean active = false;

    /**
     * Linked user account for login.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    /**
     * Getters and Setters
     */

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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