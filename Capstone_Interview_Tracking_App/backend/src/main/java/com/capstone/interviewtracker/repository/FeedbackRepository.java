package com.capstone.interviewtracker.repository;

import com.capstone.interviewtracker.model.Feedback;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Handles database operations for Feedback entity.
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * Gets all feedback for a specific interview.
     *
     * @param interviewId interview identifier
     * @return list of feedback for the interview
     */
    List<Feedback> findByInterviewId(Long interviewId);

    /**
     * Gets all feedback given by a panel member.
     *
     * @param panelId panel member identifier
     * @return list of feedback given by the panel
     */
    List<Feedback> findByPanelId(Long panelId);

    /**
     * Gets all feedback related to a candidate.
     *
     * @param candidateId candidate identifier
     * @return list of feedback for the candidate
     */
    @Query("SELECT f FROM Feedback f WHERE f.interview.candidate.id = :candidateId")
    List<Feedback> findByCandidateId(@Param("candidateId") Long candidateId);

    /**
     * Checks if feedback already exists for an interview and panel.
     *
     * @param interviewId interview identifier
     * @param panelId     panel identifier
     * @return true if feedback exists, false otherwise
     */
    boolean existsByInterviewIdAndPanelId(Long interviewId, Long panelId);
}