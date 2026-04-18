package com.shinu.todo_app.dto;

import com.shinu.todo_app.entity.TodoStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO class used to transfer Todo data between client and server
// We do NOT expose Entity directly so we use DTO

public class TodoDTO {

    // title is required field
    // should not be null and must have minimum 3 characters
    @NotNull(message = "Title cannot be null")
    @Size(min = 3, message = "Title must be at least 3 characters")
    private String title;

    // optional field for extra details about todo
    private String description;

    // status of todo (PENDING / COMPLETED)
    private TodoStatus status;

    // getter method for title
    public String getTitle() {
        return title;
    }

    // setter method for title
    public void setTitle(String title) {
        this.title = title;
    }

    // getter method for description
    public String getDescription() {
        return description;
    }

    // setter method for description
    public void setDescription(String description) {
        this.description = description;
    }

    // getter method for status
    public TodoStatus getStatus() {
        return status;
    }

    // setter method for status
    public void setStatus(TodoStatus status) {
        this.status = status;
    }
}