package com.capstone.interviewtracker.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * stores password setup token for user onboarding
 * used for candidate signup and panel activation
 * token is one-time use and expires after fixed time
 */
@Entity
@Table(name = "password_setup_tokens")
public final class PasswordSetupToken {

    /**
     * primary key for token table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * random token sent in email link
     */
    @Column(nullable = false, unique = true, length = 100)
    private String token;

    /**
     * email linked to this token
     */
    @Column(nullable = false)
    private String email;

    /**
     * role of user (candidate or panel)
     */
    @Column(nullable = false, length = 20)
    private String role;

    /**
     * token creation time
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * token expiry time
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * marks if token is already used
     */
    @Column(nullable = false)
    private boolean used = false;

    public PasswordSetupToken() {
    }

    public PasswordSetupToken(String token, String email, String role,
            LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    /**
     * 
     * Getters and Setters
     */

    /**
     * @return token id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id token id
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * @return token value
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token token value
     */
    public void setToken(final String token) {
        this.token = token;
    }

    /**
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email email
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role role
     */
    public void setRole(final String role) {
        this.role = role;
    }

    /**
     * @return created time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt created time
     */
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return expiry time
     */
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * @param expiresAt expiry time
     */
    public void setExpiresAt(final LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * @return used status
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * @param used used status
     */
    public void setUsed(final boolean used) {
        this.used = used;
    }
}