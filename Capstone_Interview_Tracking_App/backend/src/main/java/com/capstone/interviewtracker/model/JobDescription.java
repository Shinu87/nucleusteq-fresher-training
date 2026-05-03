package com.capstone.interviewtracker.model;

import com.capstone.interviewtracker.enums.JobType;
import jakarta.persistence.*;

import java.util.List;

/**
 * Represents a Job Description created by HR.
 */
@Entity
@Table(name = "job_descriptions")
public final class JobDescription {

    /**
     * Primary key for job description table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Job title.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Full job description details.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Mapping many skills to one job using join table.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    /**
     * Minimum experience required.
     */
    @Column(nullable = false)
    private Integer minExperience;

    /**
     * Maximum experience allowed.
     */
    @Column(nullable = false)
    private Integer maxExperience;

    /**
     * Minimum salary range.
     */
    @Column(nullable = false)
    private Double minSalary;

    /**
     * Maximum salary range.
     */
    @Column(nullable = false)
    private Double maxSalary;

    /**
     * Job location.
     */
    @Column(nullable = false)
    private String location;

    /**
     * Job type like FULL_TIME, CONTRACT, REMOTE.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Column(nullable = false)
    private boolean active = true;

    /**
     * Getters and Setters
     */

    /**
     * @return job id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return job title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title job title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @return job description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description job description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return list of skills
     */
    public List<Skill> getSkills() {
        return skills;
    }

    /**
     * @param skills list of skills
     */
    public void setSkills(final List<Skill> skills) {
        this.skills = skills;
    }

    /**
     * @return minimum experience
     */
    public Integer getMinExperience() {
        return minExperience;
    }

    /**
     * @param minExperience minimum experience
     */
    public void setMinExperience(final Integer minExperience) {
        this.minExperience = minExperience;
    }

    /**
     * @return maximum experience
     */
    public Integer getMaxExperience() {
        return maxExperience;
    }

    /**
     * @param maxExperience maximum experience
     */
    public void setMaxExperience(final Integer maxExperience) {
        this.maxExperience = maxExperience;
    }

    /**
     * @return minimum salary
     */
    public Double getMinSalary() {
        return minSalary;
    }

    /**
     * @param minSalary minimum salary
     */
    public void setMinSalary(final Double minSalary) {
        this.minSalary = minSalary;
    }

    /**
     * @return maximum salary
     */
    public Double getMaxSalary() {
        return maxSalary;
    }

    /**
     * @param maxSalary maximum salary
     */
    public void setMaxSalary(final Double maxSalary) {
        this.maxSalary = maxSalary;
    }

    /**
     * @return job location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location job location
     */
    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * @return job type
     */
    public JobType getJobType() {
        return jobType;
    }

    /**
     * @param jobType job type
     */
    public void setJobType(final JobType jobType) {
        this.jobType = jobType;
    }

    /**
     * @return active status
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active active status
     */
    public void setActive(final boolean active) {
        this.active = active;
    }
}