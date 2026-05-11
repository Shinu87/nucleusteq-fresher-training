package com.capstone.interviewtracker.dto.Request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO used for creating Skill entries.
 */
public class SkillRequestDTO {

    @NotBlank(message = "Skill name is required")
    private String name;

    /**
     * Getters and Setters
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}