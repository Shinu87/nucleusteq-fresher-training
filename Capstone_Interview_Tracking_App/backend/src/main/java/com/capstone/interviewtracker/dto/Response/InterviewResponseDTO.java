package com.capstone.interviewtracker.dto.Response;

import com.capstone.interviewtracker.enums.InterviewStatus;
import com.capstone.interviewtracker.enums.Stage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO used for returning interview details.
 */
public class InterviewResponseDTO {

    private Long id;
    private Stage stage;
    private LocalDateTime scheduledAt;
    private String focusArea;

    private Long candidateId;
    private String candidateName;

    private List<String> panelNames;

    private InterviewStatus status;

    /**
     * Getters and Setters
     */

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

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public List<String> getPanelNames() {
        return panelNames;
    }

    public void setPanelNames(List<String> panelNames) {
        this.panelNames = panelNames;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }
}