package com.capstone.interviewtracker.model;

import com.capstone.interviewtracker.enums.JobType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "job_descriptions")
public class JobDescription {

    // primary key for job description table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // job title
    @NotBlank
    @Column(nullable = false)
    private String title;

    // full job description details
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /*
     * mapping many skills to one job using join table
     * this will create a separate table job_skills (job_id, skill_id)
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    // minimum experience required
    @NotNull
    @Column(nullable = false)
    private Integer minExperience;

    // maximum experience allowed
    @NotNull
    @Column(nullable = false)
    private Integer maxExperience;

    // minimum salary range
    @NotNull
    @Column(nullable = false)
    private Double minSalary;

    // maximum salary range
    @NotNull
    @Column(nullable = false)
    private Double maxSalary;

    // job location
    @NotBlank
    @Column(nullable = false)
    private String location;

    // job type like FULL_TIME, CONTRACT, REMOTE
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    // default constructor
    public JobDescription() {
    }

    public JobDescription(String title, String description,
            List<Skill> skills,
            Integer minExperience, Integer maxExperience,
            Double minSalary, Double maxSalary,
            String location, JobType jobType) {
        this.title = title;
        this.description = description;
        this.skills = skills;
        this.minExperience = minExperience;
        this.maxExperience = maxExperience;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.location = location;
        this.jobType = jobType;
    }

    // getters and setters for all fields below

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