package com.capstone.interviewtracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capstone.interviewtracker.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}