package com.capstone.interviewtracker.repository;

import com.capstone.interviewtracker.model.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {
}