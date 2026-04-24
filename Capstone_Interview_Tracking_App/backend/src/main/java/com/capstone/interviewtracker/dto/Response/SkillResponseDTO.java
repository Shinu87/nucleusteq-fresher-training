package com.capstone.interviewtracker.dto.Response;

/**
 * DTO used for returning Skill details to client.
 */
public class SkillResponseDTO {

    private Long id;
    private String name;

    /**
     * Getters and Setters
     */

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
}