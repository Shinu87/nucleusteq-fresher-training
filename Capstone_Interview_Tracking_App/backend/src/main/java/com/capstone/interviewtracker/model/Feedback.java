package com.capstone.interviewtracker.model;

import com.capstone.interviewtracker.enums.FeedbackStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// This class represents the feedback given by panel after interview
@Entity
@Table(name = "feedbacks", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "interview_id", "panel_id" })
})
public class Feedback {

    // Primary key of feedback table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mandatory comments given by panel
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String comments;

    // Strengths observed in candidate
    @Column(columnDefinition = "TEXT")
    private String strengths;

    // Weaknesses observed in candidate
    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    // Topics or areas covered in interview
    @Column(columnDefinition = "TEXT")
    private String areasCovered;

    // Rating given by panel (1 to 5)
    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    // Status of feedback like SELECTED, REJECTED
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus status;

    // Many feedbacks can belong to one interview
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    // Many feedbacks can be given by one panel member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_id", nullable = false)
    private Panel panel;

    // Getter and Setter methods
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

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public Panel getPanel() {
        return panel;
    }

    public void setPanel(Panel panel) {
        this.panel = panel;
    }
}