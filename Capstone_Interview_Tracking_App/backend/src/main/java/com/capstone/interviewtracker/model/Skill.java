package com.capstone.interviewtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "skills")
public class Skill {

    // primary key for skills table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // skill name.
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    // mapping back to job descriptions
    @ManyToMany(mappedBy = "skills")
    private List<JobDescription> jobDescriptions;

    public Skill() {
    }

    public Skill(String name) {
        this.name = name;
    }

    // getter for id
    public Long getId() {
        return id;
    }

    // getter and setter for skill name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // getter and setter for related job descriptions
    public List<JobDescription> getJobDescriptions() {
        return jobDescriptions;
    }

    public void setJobDescriptions(List<JobDescription> jobDescriptions) {
        this.jobDescriptions = jobDescriptions;
    }
}