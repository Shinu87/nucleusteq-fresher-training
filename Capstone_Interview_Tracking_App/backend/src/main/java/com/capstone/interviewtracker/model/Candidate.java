package com.capstone.interviewtracker.model;

import java.time.LocalDateTime;

import com.capstone.interviewtracker.enums.CandidateStatus;
import com.capstone.interviewtracker.enums.Stage;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "candidates")
public class Candidate {
    // primary key for candidate table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // candidate full name
    @NotBlank
    @Column(nullable = false)
    private String name;

    // mapping candidate with user account
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // email should be unique and valid format
    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    // phone number of candidate
    @NotBlank
    @Column(nullable = false, unique = true)
    private String phone;

    // resume link stored as URL
    private String resumeUrl;

    // current company where candidate is working
    private String currentOrganization;

    // total years of experience
    @NotNull
    @Column(nullable = false)
    private Integer totalExperience;

    // relevant experience for job role
    private Integer relevantExperience;

    // current salary of candidate
    private Double currentCTC;

    // expected salary of candidate
    private Double expectedCTC;

    // notice period of candidate
    private String noticePeriod;

    // preferred job location
    private String preferredLocation;

    // source from where candidate came like linkedin, referral.
    private String source;

    // current stage in interview process
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stage currentStage;

    // current status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateStatus status;

    // mapping candidate to job description - many candidates can apply to one job
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobDescription jobDescription;

    // last updated timestamp for candidate record
    private LocalDateTime lastUpdatedAt;

    // getters and setters for all fields below
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public String getCurrentOrganization() {
        return currentOrganization;
    }

    public void setCurrentOrganization(String currentOrganization) {
        this.currentOrganization = currentOrganization;
    }

    public Integer getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(Integer totalExperience) {
        this.totalExperience = totalExperience;
    }

    public Integer getRelevantExperience() {
        return relevantExperience;
    }

    public void setRelevantExperience(Integer relevantExperience) {
        this.relevantExperience = relevantExperience;
    }

    public Double getCurrentCTC() {
        return currentCTC;
    }

    public void setCurrentCTC(Double currentCTC) {
        this.currentCTC = currentCTC;
    }

    public Double getExpectedCTC() {
        return expectedCTC;
    }

    public void setExpectedCTC(Double expectedCTC) {
        this.expectedCTC = expectedCTC;
    }

    public String getNoticePeriod() {
        return noticePeriod;
    }

    public void setNoticePeriod(String noticePeriod) {
        this.noticePeriod = noticePeriod;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    public JobDescription getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(JobDescription jobDescription) {
        this.jobDescription = jobDescription;
    }

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.lastUpdatedAt = LocalDateTime.now();
    }
}