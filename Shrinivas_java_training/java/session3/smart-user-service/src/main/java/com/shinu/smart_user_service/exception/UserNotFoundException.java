package com.shinu.smart_user_service.exception;

// Custom exception for user not found cases
public class UserNotFoundException extends RuntimeException {

    // Constructor to pass custom error message
    public UserNotFoundException(String message) {
        super(message); // calling parent class constructor
    }

}