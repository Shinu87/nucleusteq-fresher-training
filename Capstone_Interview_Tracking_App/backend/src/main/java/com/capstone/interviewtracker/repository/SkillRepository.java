package com.capstone.interviewtracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capstone.interviewtracker.model.Skill;

/**
 * Repository for Skill entity.
 * Provides database operations for skills.
 */
public interface SkillRepository extends JpaRepository<Skill, Long> {
}