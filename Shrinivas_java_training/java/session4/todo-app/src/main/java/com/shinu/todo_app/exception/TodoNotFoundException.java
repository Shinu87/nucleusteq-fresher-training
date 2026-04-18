package com.shinu.todo_app.exception;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(String message) {
        super(message);
    }

}
