package com.capstone.interviewtracker.model;

import com.capstone.interviewtracker.enums.InterviewStatus;
import com.capstone.interviewtracker.enums.Stage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

// This class represents an Interview scheduled for a candidate
@Entity
@Table(name = "interviews")
public class Interview {

    // Primary key of interview table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stage of interview like L1, L2, HR
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stage stage;

    // Date and time when interview is scheduled
    @NotNull
    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    // Focus area of interview like DSA, Java, System Design
    @Column(columnDefinition = "TEXT")
    private String focusArea;

    // Many interviews belong to one candidate
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    // Many panels can take one interview and one panel can attend multiple
    // interviews
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "interview_panels", joinColumns = @JoinColumn(name = "interview_id"), inverseJoinColumns = @JoinColumn(name = "panel_id"))
    @Size(min = 1, max = 2)
    private List<Panel> panels;

    @Enumerated(EnumType.STRING)
    private InterviewStatus status;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public List<Panel> getPanels() {
        return panels;
    }

    public void setPanels(List<Panel> panels) {
        this.panels = panels;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }
}