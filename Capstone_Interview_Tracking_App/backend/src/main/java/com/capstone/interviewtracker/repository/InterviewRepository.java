package com.capstone.interviewtracker.repository;

import com.capstone.interviewtracker.enums.Stage;
import com.capstone.interviewtracker.model.Interview;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Interview repository.
 * Handles database operations for interview table.
 */
public interface InterviewRepository extends JpaRepository<Interview, Long> {

        /**
         * Gets all interviews of a candidate with panel details.
         *
         * @param candidateId candidate identifier
         * @return list of interviews with panels
         */
        @Query("SELECT DISTINCT i FROM Interview i " +
                        "LEFT JOIN FETCH i.panels " +
                        "WHERE i.candidate.id = :candidateId")
        List<Interview> findByCandidateIdWithPanels(
                        @Param("candidateId") Long candidateId);

        /**
         * Gets interview of a candidate by stage.
         * Used for specific round like tech or HR.
         *
         * @param candidateId candidate identifier
         * @param stage       interview stage
         * @return interview if found
         */
        @Query("SELECT i FROM Interview i " +
                        "LEFT JOIN FETCH i.panels " +
                        "WHERE i.candidate.id = :candidateId AND i.stage = :stage")
        Optional<Interview> findByCandidateIdAndStage(
                        @Param("candidateId") Long candidateId,
                        @Param("stage") Stage stage);

        /**
         * Gets all interviews with panel details.
         *
         * @return list of all interviews with panels
         */
        @Query("SELECT DISTINCT i FROM Interview i LEFT JOIN FETCH i.panels")
        List<Interview> findAllWithPanels();

        /**
         * Checks if interview already exists for candidate and stage.
         *
         * @param candidateId candidate identifier
         * @param stage       interview stage
         * @return true if exists, false otherwise
         */
        boolean existsByCandidateIdAndStage(Long candidateId, Stage stage);

        /**
         * Checks whether a candidate already has an interview at the given stage.
         *
         * @param candidateId   candidate identifier
         * @param applicationId current application cycle counter
         * @param stage         interview stage
         * @return true if a current-cycle interview exists at this stage
         */
        boolean existsByCandidateIdAndApplicationIdAndStage(
                        Long candidateId, Integer applicationId, Stage stage);

        /**
         * Returns the candidate's interview at a given stage.
         *
         * @param candidateId   candidate identifier
         * @param applicationId current application cycle counter
         * @param stage         interview stage
         * @return interview if found
         */
        @Query("SELECT i FROM Interview i " +
                        "LEFT JOIN FETCH i.panels " +
                        "WHERE i.candidate.id = :candidateId " +
                        "  AND i.applicationId = :applicationId " +
                        "  AND i.stage = :stage")
        Optional<Interview> findByCandidateIdAndApplicationIdAndStage(
                        @Param("candidateId") Long candidateId,
                        @Param("applicationId") Integer applicationId,
                        @Param("stage") Stage stage);

}