package com.capstone.interviewtracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.capstone.interviewtracker.enums.CandidateStatus;
import com.capstone.interviewtracker.enums.Stage;
import jakarta.persistence.*;

/**
 * Represents a candidate in the interview tracking system.
 */
@Entity
@Table(name = "candidates")
public final class Candidate {

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
     * Candidate email used for identification.
     */
    @Column(nullable = false)
    private String email;

    /**
     * Candidate phone number used for contact.
     */
    @Column(nullable = false)
    private String phone;

    /**
     * Candidate date of birth. Age is derived dynamically from this field.
     */
    @Column(nullable = true)
    private LocalDate dateOfBirth;

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
     * Stores the application cycle number for a candidate.
     */
    @Column(nullable = true)
    private Integer applicationId;

    /**
     * Last updated timestamp for candidate record.
     */
    private LocalDateTime lastUpdatedAt;

    /**
     * Gets candidate ID.
     *
     * @return candidate id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets candidate ID.
     *
     * @param id candidate id
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * Gets candidate name.
     *
     * @return candidate name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets candidate name.
     *
     * @param name candidate name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets candidate email.
     *
     * @return candidate email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets candidate email.
     *
     * @param email candidate email
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Gets candidate phone number.
     *
     * @return candidate phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets candidate phone number.
     *
     * @param phone candidate phone
     */
    public void setPhone(final String phone) {
        this.phone = phone;
    }

    /**
     * Gets candidate date of birth.
     *
     * @return date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets candidate date of birth.
     *
     * @param dateOfBirth date of birth
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Derives the candidate's age from date of birth.
     *
     * @return calculated age in years, or null if dateOfBirth is not set
     */
    public Integer getAge() {
        if (dateOfBirth == null)
            return null;
        return java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Gets resume URL.
     *
     * @return resume url
     */
    public String getResumeUrl() {
        return resumeUrl;
    }

    /**
     * Sets resume URL.
     *
     * @param resumeUrl resume url
     */
    public void setResumeUrl(final String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    /**
     * Gets current organization.
     *
     * @return organization
     */
    public String getCurrentOrganization() {
        return currentOrganization;
    }

    /**
     * Sets current organization.
     *
     * @param currentOrganization organization
     */
    public void setCurrentOrganization(final String currentOrganization) {
        this.currentOrganization = currentOrganization;
    }

    /**
     * Gets total experience.
     *
     * @return total experience
     */
    public Integer getTotalExperience() {
        return totalExperience;
    }

    /**
     * Sets total experience.
     *
     * @param totalExperience total experience
     */
    public void setTotalExperience(final Integer totalExperience) {
        this.totalExperience = totalExperience;
    }

    /**
     * Gets relevant experience.
     *
     * @return relevant experience
     */
    public Integer getRelevantExperience() {
        return relevantExperience;
    }

    /**
     * Sets relevant experience.
     *
     * @param relevantExperience relevant experience
     */
    public void setRelevantExperience(final Integer relevantExperience) {
        this.relevantExperience = relevantExperience;
    }

    /**
     * Gets current CTC.
     *
     * @return current ctc
     */
    public Double getCurrentCTC() {
        return currentCTC;
    }

    /**
     * Sets current CTC.
     *
     * @param currentCTC current ctc
     */
    public void setCurrentCTC(final Double currentCTC) {
        this.currentCTC = currentCTC;
    }

    /**
     * Gets expected CTC.
     *
     * @return expected ctc
     */
    public Double getExpectedCTC() {
        return expectedCTC;
    }

    /**
     * Sets expected CTC.
     *
     * @param expectedCTC expected ctc
     */
    public void setExpectedCTC(final Double expectedCTC) {
        this.expectedCTC = expectedCTC;
    }

    /**
     * Gets notice period.
     *
     * @return notice period
     */
    public String getNoticePeriod() {
        return noticePeriod;
    }

    /**
     * Sets notice period.
     *
     * @param noticePeriod notice period
     */
    public void setNoticePeriod(final String noticePeriod) {
        this.noticePeriod = noticePeriod;
    }

    /**
     * Gets preferred location.
     *
     * @return preferred location
     */
    public String getPreferredLocation() {
        return preferredLocation;
    }

    /**
     * Sets preferred location.
     *
     * @param preferredLocation preferred location
     */
    public void setPreferredLocation(final String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    /**
     * Gets source of candidate.
     *
     * @return source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets source of candidate.
     *
     * @param source source
     */
    public void setSource(final String source) {
        this.source = source;
    }

    /**
     * Gets current stage.
     *
     * @return stage
     */
    public Stage getCurrentStage() {
        return currentStage;
    }

    /**
     * Sets current stage.
     *
     * @param currentStage stage
     */
    public void setCurrentStage(final Stage currentStage) {
        this.currentStage = currentStage;
    }

    /**
     * Gets candidate status.
     *
     * @return status
     */
    public CandidateStatus getStatus() {
        return status;
    }

    /**
     * Sets candidate status.
     *
     * @param status status
     */
    public void setStatus(final CandidateStatus status) {
        this.status = status;
    }

    /**
     * Gets job description.
     *
     * @return job description
     */
    public JobDescription getJobDescription() {
        return jobDescription;
    }

    /**
     * Sets job description.
     *
     * @param jobDescription job description
     */
    public void setJobDescription(final JobDescription jobDescription) {
        this.jobDescription = jobDescription;
    }

    /**
     * Gets application id.
     *
     * @return application id
     */
    public Integer getApplicationId() {
        return applicationId;
    }

    /**
     * Sets application id.
     *
     * @param applicationId application id
     */
    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * Updates timestamp before insert and update operations.
     */
    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * @return user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user user
     */
    public void setUser(final User user) {
        this.user = user;
    }
}