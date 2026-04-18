package com.shinu.todo_app.entity;

// This enum represents status of a Todo task
// It restricts values to only PENDING or COMPLETED
// This helps avoid invalid status values in the application

public enum TodoStatus {
    PENDING, // task is not completed yet
    COMPLETED // task has been finished
}