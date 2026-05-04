package com.capstone.interviewtracker.repository;

import com.capstone.interviewtracker.model.JobDescription;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for JobDescription entity.
 * Handles database operations for job descriptions.
 */
public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {

    /**
     * Fetches all job descriptions along with their skills in one query.
     *
     * @return list of job descriptions with skills
     */
    @Query("SELECT DISTINCT j FROM JobDescription j LEFT JOIN FETCH j.skills")
    List<JobDescription> findAllWithSkills();

    /**
     * Gets only active job descriptions with their skills.
     * Inactive jobs are excluded.
     *
     * @return list of active job descriptions with skills
     */
    @Query("SELECT DISTINCT j FROM JobDescription j " +
            "LEFT JOIN FETCH j.skills WHERE j.active = true")
    List<JobDescription> findAllActiveWithSkills();
}