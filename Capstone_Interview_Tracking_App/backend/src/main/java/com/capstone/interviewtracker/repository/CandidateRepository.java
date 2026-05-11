package com.capstone.interviewtracker.repository;

import com.capstone.interviewtracker.enums.CandidateStatus;
import com.capstone.interviewtracker.model.Candidate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for Candidate entity.
 * Provides database operations for Candidate module using Spring Data JPA.
 * Extends JpaRepository to get built-in CRUD methods.
 */
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

        /**
         * Checks if a candidate exists with the given email.
         *
         * @param email candidate email
         * @return true if exists, false otherwise
         */
        boolean existsByEmail(String email);

        /**
         * Checks if a candidate exists with the given phone number.
         *
         * @param phone candidate phone number
         * @return true if exists, false otherwise
         */
        boolean existsByPhone(String phone);

        /**
         * Finds candidate by user id.
         *
         * @param userId user id linked to candidate
         * @return candidate if found
         */
        Optional<Candidate> findByUserId(Long userId);

        /**
         * Finds candidate by email address.
         * Used by the "my application" endpoint.
         *
         * @param email candidate email
         * @return candidate if found
         */
        Optional<Candidate> findByEmail(String email);

        /**
         * Finds all active (non-rejected) candidates by email.
         * Used to enforce one-application-at-a-time rule.
         *
         * @param email          candidate email
         * @param rejectedStatus rejected status enum
         * @return list of active candidates
         */
        @Query("SELECT c FROM Candidate c " +
                        "WHERE c.email = :email AND c.status <> :rejectedStatus")
        List<Candidate> findActiveByEmail(@Param("email") String email,
                        @Param("rejectedStatus") CandidateStatus rejectedStatus);

        /**
         * Finds all active (non-rejected) candidates by phone.
         *
         * @param phone          candidate phone number
         * @param rejectedStatus rejected status enum
         * @return list of active candidates
         */
        @Query("SELECT c FROM Candidate c " +
                        "WHERE c.phone = :phone AND c.status <> :rejectedStatus")
        List<Candidate> findActiveByPhone(@Param("phone") String phone,
                        @Param("rejectedStatus") CandidateStatus rejectedStatus);
}