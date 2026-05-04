package com.capstone.interviewtracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capstone.interviewtracker.model.PasswordSetupToken;

/**
 * Repository for password setup tokens.
 * Handles database operations related to password setup tokens.
 */
public interface PasswordSetupTokenRepository
        extends JpaRepository<PasswordSetupToken, Long> {

    /**
     * Finds password setup token by token string.
     *
     * @param token unique token from email link
     * @return password setup token if found
     */
    Optional<PasswordSetupToken> findByToken(String token);
}