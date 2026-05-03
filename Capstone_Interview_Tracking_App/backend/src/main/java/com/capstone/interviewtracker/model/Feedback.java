package com.capstone.interviewtracker.model;

import com.capstone.interviewtracker.enums.FeedbackStatus;
import jakarta.persistence.*;

/**
 * Represents feedback given by a panel member after an interview.
 */
@Entity
@Table(name = "feedbacks", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "interview_id", "panel_id" })
})
public final class Feedback {

    /**
     * Primary key of feedback table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mandatory comments given by panel.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String comments;

    /**
     * Strengths observed in candidate.
     */
    @Column(columnDefinition = "TEXT")
    private String strengths;

    /**
     * Weaknesses observed in candidate.
     */
    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    /**
     * Topics or areas covered in interview.
     */
    @Column(columnDefinition = "TEXT")
    private String areasCovered;

    /**
     * Rating given by panel (1 to 5).
     */
    @Column(nullable = false)
    private Integer rating;

    /**
     * Status of feedback like SELECTED or REJECTED.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus status;

    /**
     * Many feedbacks belong to one interview.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    /**
     * Many feedbacks can be given by one panel member.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_id", nullable = false)
    private Panel panel;

    /**
     * Getters and Setters
     */

    /**
     * @return feedback id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id feedback id
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * @return comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * @param comments comments
     */
    public void setComments(final String comments) {
        this.comments = comments;
    }

    /**
     * @return strengths
     */
    public String getStrengths() {
        return strengths;
    }

    /**
     * @param strengths strengths
     */
    public void setStrengths(final String strengths) {
        this.strengths = strengths;
    }

    /**
     * @return weaknesses
     */
    public String getWeaknesses() {
        return weaknesses;
    }

    /**
     * @param weaknesses weaknesses
     */
    public void setWeaknesses(final String weaknesses) {
        this.weaknesses = weaknesses;
    }

    /**
     * @return areas covered
     */
    public String getAreasCovered() {
        return areasCovered;
    }

    /**
     * @param areasCovered areas covered
     */
    public void setAreasCovered(final String areasCovered) {
        this.areasCovered = areasCovered;
    }

    /**
     * @return rating
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * @param rating rating
     */
    public void setRating(final Integer rating) {
        this.rating = rating;
    }

    /**
     * @return feedback status
     */
    public FeedbackStatus getStatus() {
        return status;
    }

    /**
     * @param status feedback status
     */
    public void setStatus(final FeedbackStatus status) {
        this.status = status;
    }

    /**
     * @return interview
     */
    public Interview getInterview() {
        return interview;
    }

    /**
     * @param interview interview
     */
    public void setInterview(final Interview interview) {
        this.interview = interview;
    }

    /**
     * @return panel
     */
    public Panel getPanel() {
        return panel;
    }

    /**
     * @param panel panel
     */
    public void setPanel(final Panel panel) {
        this.panel = panel;
    }
}