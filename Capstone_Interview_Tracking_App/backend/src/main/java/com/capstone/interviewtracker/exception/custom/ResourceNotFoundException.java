package com.capstone.interviewtracker.exception.custom;

/**
 * Exception thrown when a requested resource is not found in the system.
 *
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates exception with message.
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }

    /**
     * Creates exception using resource details.
     */
    public ResourceNotFoundException(
            final String resourceName,
            final String fieldName,
            final Object fieldValue) {

        super(resourceName + " not found with " + fieldName + ": " + fieldValue);
    }
}