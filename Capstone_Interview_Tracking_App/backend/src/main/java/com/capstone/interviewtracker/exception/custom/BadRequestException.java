package com.capstone.interviewtracker.exception.custom;

/**
 * Exception thrown when a request is valid in format but not valid in logic.
 *
 */
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates exception with message.
     */
    public BadRequestException(final String message) {
        super(message);
    }

    /**
     * Creates exception with message and cause.
     */
    public BadRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}