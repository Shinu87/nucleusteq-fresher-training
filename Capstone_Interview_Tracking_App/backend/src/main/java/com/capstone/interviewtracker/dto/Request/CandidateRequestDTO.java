package com.capstone.interviewtracker.dto.Request;

import com.capstone.interviewtracker.enums.CandidateStatus;
import com.capstone.interviewtracker.enums.Stage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO used for creating and updating Candidate details.
 * Contains all input fields required from API requests.
 */
public class CandidateRequestDTO {

    /**
     * Candidate full name.
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * Email of the candidate.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Phone number of candidate.
     */
    @NotBlank(message = "Phone is required")
    private String phone;

    /**
     * Candidate age.
     * Must be between 18 and 60 as per eligibility rules.
     */
    @Min(value = 18, message = "Candidate must be at least 18 years old to apply for jobs")
    @Max(value = 60, message = "Candidate exceeds maximum eligible working age")
    private Integer age;

    /**
     * Resume URL.
     */
    private String resumeUrl;

    /**
     * Current organization.
     */
    private String currentOrganization;

    /**
     * Total experience in years.
     */
    @NotNull(message = "Total experience is required")
    private Integer totalExperience;

    /**
     * Relevant experience in years.
     */
    private Integer relevantExperience;

    /**
     * Current CTC.
     */
    private Double currentCTC;

    /**
     * Expected CTC.
     */
    private Double expectedCTC;

    /**
     * Notice period.
     */
    private String noticePeriod;

    /**
     * Preferred location.
     */
    private String preferredLocation;

    /**
     * Source of candidate like LinkedIn, referral.
     */
    private String source;

    /**
     * Current stage in interview process.
     */
    @NotNull(message = "Stage is required")
    private Stage currentStage;

    /**
     * Current status of candidate.
     */
    @NotNull(message = "Status is required")
    private CandidateStatus status;

    /**
     * Job ID applied for.
     */
    @NotNull(message = "Job ID is required")
    private Long jobId;

    /**
     * Getters and Setters
     */

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
}