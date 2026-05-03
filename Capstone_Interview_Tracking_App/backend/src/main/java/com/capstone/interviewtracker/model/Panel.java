package com.capstone.interviewtracker.model;

import jakarta.persistence.*;

/**
 * Represents panel members who conduct interviews in the system.
 */
@Entity
@Table(name = "panels")
public final class Panel {

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

    /**
     * @return user entity
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user user entity
     */
    public void setUser(final User user) {
        this.user = user;
    }

    /**
     * @return panel id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id panel id
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * @return panel name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name panel name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return panel email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email panel email
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * @return panel mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile panel mobile
     */
    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return organization
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization organization
     */
    public void setOrganization(final String organization) {
        this.organization = organization;
    }

    /**
     * @return designation
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * @param designation designation
     */
    public void setDesignation(final String designation) {
        this.designation = designation;
    }

    /**
     * @return active status
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active active status
     */
    public void setActive(final boolean active) {
        this.active = active;
    }
}