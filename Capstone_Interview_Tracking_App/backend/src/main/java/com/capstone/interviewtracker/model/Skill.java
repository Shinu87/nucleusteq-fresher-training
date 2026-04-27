package com.capstone.interviewtracker.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a technical skill used in Job Descriptions.
 */
@Entity
@Table(name = "skills")
public class Skill {

    /**
     * Primary key for skills table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the skill.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Jobs associated with this skill.
     * Bidirectional mapping with JobDescription.
     */

    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private List<JobDescription> jobDescriptions;

    // Constructors

    public Skill() {
    }

    public Skill(String name) {
        this.name = name;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JobDescription> getJobDescriptions() {
        return jobDescriptions;
    }

    public void setJobDescriptions(List<JobDescription> jobDescriptions) {
        this.jobDescriptions = jobDescriptions;
    }
}