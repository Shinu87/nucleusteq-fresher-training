package com.shinu.todo_app.exception;

// Custom exception class for Todo not found scenario
// This is used when user tries to access or modify a Todo that does not exist

public class TodoNotFoundException extends RuntimeException {

    // constructor that passes error message to parent RuntimeException
    public TodoNotFoundException(String message) {
        super(message);
    }

}