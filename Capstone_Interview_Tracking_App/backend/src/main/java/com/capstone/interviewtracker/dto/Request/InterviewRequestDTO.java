package com.capstone.interviewtracker.dto.Request;

import com.capstone.interviewtracker.enums.InterviewStatus;
import com.capstone.interviewtracker.enums.Stage;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO used for scheduling an interview.
 */
public class InterviewRequestDTO {

    /**
     * Interview stage (L1, L2, HR).
     */
    @NotNull(message = "Stage is required")
    private Stage stage;

    /**
     * Scheduled date and time.
     */
    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledAt;

    /**
     * Focus area for interview.
     */
    private String focusArea;

    /**
     * Candidate ID.
     */
    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    /**
     * Panel IDs (min 1, max 2).
     */
    @Size(min = 1, max = 2, message = "Minimum 1 and maximum 2 panels allowed")
    private List<Long> panelIds;

    /**
     * Interview status.
     */
    private InterviewStatus status;

    /**
     * Getters and Setters
     */

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

    public List<Long> getPanelIds() {
        return panelIds;
    }

    public void setPanelIds(List<Long> panelIds) {
        this.panelIds = panelIds;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }
}
