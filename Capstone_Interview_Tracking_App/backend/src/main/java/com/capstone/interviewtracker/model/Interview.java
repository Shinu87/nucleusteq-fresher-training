package com.capstone.interviewtracker.model;

import com.capstone.interviewtracker.enums.InterviewStatus;
import com.capstone.interviewtracker.enums.Stage;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an Interview scheduled for a candidate.
 */
@Entity
@Table(name = "interviews")
public final class Interview {

    /**
     * Primary key of interview table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Stage of interview like L1, L2, HR.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stage stage;

    /**
     * Date and time when interview is scheduled.
     */
    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    /**
     * Focus area of interview like DSA, Java, System Design.
     */
    @Column(columnDefinition = "TEXT")
    private String focusArea;

    /**
     * HR enters this while scheduling. Sent to panel via email.
     */
    @Column(length = 500)
    private String meetingUrl;

    /**
     * Many interviews belong to one candidate.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    /**
     * Stores application cycle for the interview.
     */
    @Column(nullable = true)
    private Integer applicationId;

    /**
     * Many panels can take one interview and one panel can attend multiple
     * interviews.
     * Minimum 1 and maximum 2 panels allowed.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "interview_panels", joinColumns = @JoinColumn(name = "interview_id"), inverseJoinColumns = @JoinColumn(name = "panel_id"))
    private List<Panel> panels;

    /**
     * Status of the interview.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    // Getters and Setters

    /**
     * @return interview id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id interview id
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * @return interview stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @param stage interview stage
     */
    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * @return scheduled time
     */
    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    /**
     * @param scheduledAt scheduled time
     */
    public void setScheduledAt(final LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    /**
     * @return focus area
     */
    public String getFocusArea() {
        return focusArea;
    }

    /**
     * @param focusArea focus area
     */
    public void setFocusArea(final String focusArea) {
        this.focusArea = focusArea;
    }

    /**
     * @return candidate
     */
    public Candidate getCandidate() {
        return candidate;
    }

    /**
     * @param candidate candidate
     */
    public void setCandidate(final Candidate candidate) {
        this.candidate = candidate;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * @return panel list
     */
    public List<Panel> getPanels() {
        return panels;
    }

    /**
     * @param panels panel list
     */
    public void setPanels(final List<Panel> panels) {
        this.panels = panels;
    }

    /**
     * @return interview status
     */
    public InterviewStatus getStatus() {
        return status;
    }

    /**
     * @param status interview status
     */
    public void setStatus(final InterviewStatus status) {
        this.status = status;
    }

    /**
     * @return meeting url
     */
    public String getMeetingUrl() {
        return meetingUrl;
    }

    /**
     * @param meetingUrl meeting url
     */
    public void setMeetingUrl(final String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }
}