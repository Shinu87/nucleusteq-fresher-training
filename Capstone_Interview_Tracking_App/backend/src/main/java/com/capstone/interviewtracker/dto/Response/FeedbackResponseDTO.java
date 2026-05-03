package com.capstone.interviewtracker.dto.Response;

import com.capstone.interviewtracker.enums.FeedbackStatus;
import com.capstone.interviewtracker.enums.Stage;

/**
 * DTO used for sending feedback details in API response.
 */
public class FeedbackResponseDTO {

    private Long id;
    private String comments;
    private String strengths;
    private String weaknesses;
    private String areasCovered;
    private Integer rating;
    private FeedbackStatus status;

    private Long interviewId;
    private Stage interviewStage;

    private Long panelId;
    private String panelName;

    /**
     * Getters and Setters
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Stage getInterviewStage() {
        return interviewStage;
    }

    public void setInterviewStage(Stage interviewStage) {
        this.interviewStage = interviewStage;
    }

    public Long getPanelId() {
        return panelId;
    }

    public void setPanelId(Long panelId) {
        this.panelId = panelId;
    }

    public String getPanelName() {
        return panelName;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

}