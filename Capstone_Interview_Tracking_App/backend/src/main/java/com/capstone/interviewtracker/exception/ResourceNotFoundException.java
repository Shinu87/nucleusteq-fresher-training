package com.capstone.interviewtracker.exception;

/**
 * This exception is used when data is not found in database.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
