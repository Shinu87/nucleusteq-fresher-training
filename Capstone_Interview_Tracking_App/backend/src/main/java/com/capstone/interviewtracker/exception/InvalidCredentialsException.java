package com.capstone.interviewtracker.exception;

/**
 * This exception is used when user enters wrong login details
 * like email or password.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

}
