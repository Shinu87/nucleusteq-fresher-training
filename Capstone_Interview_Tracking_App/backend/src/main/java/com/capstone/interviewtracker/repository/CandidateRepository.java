package com.capstone.interviewtracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capstone.interviewtracker.model.Candidate;

/**
 * Repository interface for Candidate entity.
 * Provides database operations for Candidate module using Spring Data JPA.
 * Extends JpaRepository to get built-in CRUD methods.
 */
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String Phone);

    Optional<Candidate> findByUserId(Long userId);

}
