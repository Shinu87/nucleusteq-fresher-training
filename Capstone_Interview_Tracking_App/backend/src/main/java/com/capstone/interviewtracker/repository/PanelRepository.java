package com.capstone.interviewtracker.repository;

import com.capstone.interviewtracker.model.Panel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Panel repository used to interact with database for Panel table
 */
public interface PanelRepository extends JpaRepository<Panel, Long> {

    /**
     * check if email already exists
     */
    boolean existsByEmail(String email);

    /**
     * check if mobile already exists
     */
    boolean existsByMobile(String mobile);

    /**
     * get only active panels
     */
    List<Panel> findByActiveTrue();
}