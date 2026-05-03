package com.capstone.interviewtracker.exception.custom;

/**
 * Exception thrown when user authentication or authorization fails.
 *
 */
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates exception with message.
     */
    public UnauthorizedException(final String message) {
        super(message);
    }

    /**
     * Creates exception with message and cause.
     */
    public UnauthorizedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}