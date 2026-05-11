package com.capstone.interviewtracker.model;

import com.capstone.interviewtracker.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Represents application users for authentication and authorization.
 * Users can be HR, PANEL, or CANDIDATE based on role.
 */
@Entity
@Table(name = "users")
public final class User {

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
    @Column(nullable = true)
    private String password;

    /** candidate mobile number. */
    @Column(nullable = true)
    private String mobile;

    /** candidate gender. */
    @Column(nullable = true, length = 20)
    private String gender;

    /**
     * Candidate date of birth captured at signup.
     * Age is derived dynamically from this field.
     */
    @Column(nullable = true)
    private LocalDate dateOfBirth;

    /**
     * Role of the user (HR / PANEL / CANDIDATE).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * When false the account is deactivated — user cannot log in.
     * to keep new accounts disabled until they set their password.
     */
    @Column(nullable = false)
    private boolean enabled = true;

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

    /**
     * @return user id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name user name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return user email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email user email
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * @return user password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password user password
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * @return user role
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role user role
     */
    public void setRole(final Role role) {
        this.role = role;
    }

    /**
     * @return mobile number
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile mobile number
     */
    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender gender
     */
    public void setGender(final String gender) {
        this.gender = gender;
    }

    /**
     * @return date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * @param dateOfBirth date of birth
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Derives the candidates age from date of birth.
     *
     * @return calculated age in years, or null if dateOfBirth is not set
     */
    public Integer getAge() {
        if (dateOfBirth == null)
            return null;
        return java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * @return enabled status
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled enabled status
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}