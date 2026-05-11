package com.capstone.interviewtracker.exception.custom;

/**
 * Exception thrown when a request fails due to a conflict with existing data.
 *
 */
public class ConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates exception with message.
     */
    public ConflictException(final String message) {
        super(message);
    }

    /**
     * Creates exception with message and cause.
     */
    public ConflictException(final String message, final Throwable cause) {
        super(message, cause);
    }
}