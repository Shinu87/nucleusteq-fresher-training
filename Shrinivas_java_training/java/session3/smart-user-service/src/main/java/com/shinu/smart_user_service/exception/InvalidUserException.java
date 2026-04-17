package com.shinu.smart_user_service.exception;

// Custom exception for invalid user input
public class InvalidUserException extends RuntimeException {

    // Constructor to pass custom error message
    public InvalidUserException(String message) {
        super(message); // calling parent class constructor
    }
}