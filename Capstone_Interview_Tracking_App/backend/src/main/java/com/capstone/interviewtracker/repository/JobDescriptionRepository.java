package com.capstone.interviewtracker.repository;

import com.capstone.interviewtracker.model.JobDescription;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {

    /**
     * Fetches all job descriptions along with their skills in one query.
     */
    @Query("SELECT DISTINCT j FROM JobDescription j LEFT JOIN FETCH j.skills")
    List<JobDescription> findAllWithSkills();
}