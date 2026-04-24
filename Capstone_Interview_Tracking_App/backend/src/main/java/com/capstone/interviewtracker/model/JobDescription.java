package com.capstone.interviewtracker.model;

import com.capstone.interviewtracker.enums.JobType;
import jakarta.persistence.*;

import java.util.List;

/**
 * Represents a Job Description created by HR.
 */
@Entity
@Table(name = "job_descriptions")
public class JobDescription {

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

    /**
     * Getters and Setters
     */

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public Integer getMinExperience() {
        return minExperience;
    }

    public void setMinExperience(Integer minExperience) {
        this.minExperience = minExperience;
    }

    public Integer getMaxExperience() {
        return maxExperience;
    }

    public void setMaxExperience(Integer maxExperience) {
        this.maxExperience = maxExperience;
    }

    public Double getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Double minSalary) {
        this.minSalary = minSalary;
    }

    public Double getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Double maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }
}