package com.capstone.interviewtracker.dto.Request;

import com.capstone.interviewtracker.enums.FeedbackStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO used when panel submits interview feedback.
 */
public class FeedbackRequestDTO {

    /**
     * Mandatory comments from panel.
     */
    @NotBlank(message = "Comments are required")
    private String comments;

    /**
     * Strengths of candidate.
     */
    private String strengths;

    /**
     * Weaknesses of candidate.
     */
    private String weaknesses;

    /**
     * Areas covered in interview.
     */
    private String areasCovered;

    /**
     * Rating between 1 and 5.
     */
    @NotNull(message = "Rating is required")
    @Min(1)
    @Max(5)
    private Integer rating;

    /**
     * Feedback status (SELECTED / REJECTED).
     */
    @NotNull(message = "Status is required")
    private FeedbackStatus status;

    /**
     * Interview ID.
     */
    @NotNull(message = "Interview ID is required")
    private Long interviewId;

    /**
     * Panel ID.
     */
    @NotNull(message = "Panel ID is required")
    private Long panelId;

    /**
     * Getters and Setters
     */

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getAreasCovered() {
        return areasCovered;
    }

    public void setAreasCovered(String areasCovered) {
        this.areasCovered = areasCovered;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public FeedbackStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Long getPanelId() {
        return panelId;
    }

    public void setPanelId(Long panelId) {
        this.panelId = panelId;
    }
}