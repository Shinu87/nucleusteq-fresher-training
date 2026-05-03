package com.capstone.interviewtracker.repository;

import com.capstone.interviewtracker.model.Panel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Panel repository used to interact with database for Panel table.
 */
public interface PanelRepository extends JpaRepository<Panel, Long> {

    /**
     * Checks if a panel exists with the given email.
     *
     * @param email panel email
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Looks up a panel record by email address. Used to resolve the
     * auto-provisioned HR-as-panel record when scheduling HR interviews.
     *
     * @param email panel email
     * @return panel if found, empty otherwise
     */
    Optional<Panel> findByEmail(String email);

    /**
     * Checks if a panel exists with the given mobile number.
     *
     * @param mobile panel mobile number
     * @return true if exists, false otherwise
     */
    boolean existsByMobile(String mobile);

    /**
     * Gets only active panels.
     *
     * @return list of active panels
     */
    List<Panel> findByActiveTrue();
}