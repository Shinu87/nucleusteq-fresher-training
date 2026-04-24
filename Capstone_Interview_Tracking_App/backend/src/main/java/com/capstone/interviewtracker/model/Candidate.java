package com.capstone.interviewtracker.model;

import java.time.LocalDateTime;

import com.capstone.interviewtracker.enums.CandidateStatus;
import com.capstone.interviewtracker.enums.Stage;
import jakarta.persistence.*;

/**
 * Represents a candidate in the interview tracking system.
 */
@Entity
@Table(name = "candidates")
public class Candidate {

    /**
     * Primary key for candidate table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Candidate full name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Mapping candidate with user account.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    /**
     * Email of the candidate should be unique.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Phone number of candidate.
     */
    @Column(nullable = false, unique = true)
    private String phone;

    /**
     * Resume link stored as URL.
     */
    private String resumeUrl;

    /**
     * Current company where candidate is working.
     */
    private String currentOrganization;

    /**
     * Total years of experience.
     */
    @Column(nullable = false)
    private Integer totalExperience;

    /**
     * Relevant experience for job role.
     */
    private Integer relevantExperience;

    /**
     * Current salary of candidate.
     */
    private Double currentCTC;

    /**
     * Expected salary of candidate.
     */
    private Double expectedCTC;

    /**
     * Notice period of candidate.
     */
    private String noticePeriod;

    /**
     * Preferred job location.
     */
    private String preferredLocation;

    /**
     * Source from where candidate came like LinkedIn, referral.
     */
    private String source;

    /**
     * Current stage in interview process.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stage currentStage;

    /**
     * Current status of the candidate.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateStatus status;

    /**
     * Mapping candidate to job description.
     * Many candidates can apply to one job.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobDescription jobDescription;

    /**
     * Last updated timestamp for candidate record.
     */
    private LocalDateTime lastUpdatedAt;

    /**
     * Gets candidate ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets candidate ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets candidate name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets candidate name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets candidate email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets candidate email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets candidate phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets candidate phone number.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets resume URL.
     */
    public String getResumeUrl() {
        return resumeUrl;
    }

    /**
     * Sets resume URL.
     */
    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    /**
     * Gets current organization.
     */
    public String getCurrentOrganization() {
        return currentOrganization;
    }

    /**
     * Sets current organization.
     */
    public void setCurrentOrganization(String currentOrganization) {
        this.currentOrganization = currentOrganization;
    }

    /**
     * Gets total experience.
     */
    public Integer getTotalExperience() {
        return totalExperience;
    }

    /**
     * Sets total experience.
     */
    public void setTotalExperience(Integer totalExperience) {
        this.totalExperience = totalExperience;
    }

    /**
     * Gets relevant experience.
     */
    public Integer getRelevantExperience() {
        return relevantExperience;
    }

    /**
     * Sets relevant experience.
     */
    public void setRelevantExperience(Integer relevantExperience) {
        this.relevantExperience = relevantExperience;
    }

    /**
     * Gets current CTC.
     */
    public Double getCurrentCTC() {
        return currentCTC;
    }

    /**
     * Sets current CTC.
     */
    public void setCurrentCTC(Double currentCTC) {
        this.currentCTC = currentCTC;
    }

    /**
     * Gets expected CTC.
     */
    public Double getExpectedCTC() {
        return expectedCTC;
    }

    /**
     * Sets expected CTC.
     */
    public void setExpectedCTC(Double expectedCTC) {
        this.expectedCTC = expectedCTC;
    }

    /**
     * Gets notice period.
     */
    public String getNoticePeriod() {
        return noticePeriod;
    }

    /**
     * Sets notice period.
     */
    public void setNoticePeriod(String noticePeriod) {
        this.noticePeriod = noticePeriod;
    }

    /**
     * Gets preferred location.
     */
    public String getPreferredLocation() {
        return preferredLocation;
    }

    /**
     * Sets preferred location.
     */
    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    /**
     * Gets source of candidate.
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets source of candidate.
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets current stage.
     */
    public Stage getCurrentStage() {
        return currentStage;
    }

    /**
     * Sets current stage.
     */
    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    /**
     * Gets candidate status.
     */
    public CandidateStatus getStatus() {
        return status;
    }

    /**
     * Sets candidate status.
     */
    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    /**
     * Gets job description.
     */
    public JobDescription getJobDescription() {
        return jobDescription;
    }

    /**
     * Sets job description.
     */
    public void setJobDescription(JobDescription jobDescription) {
        this.jobDescription = jobDescription;
    }

    /**
     * Updates timestamp before insert and update operations.
     */
    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.lastUpdatedAt = LocalDateTime.now();
    }
}