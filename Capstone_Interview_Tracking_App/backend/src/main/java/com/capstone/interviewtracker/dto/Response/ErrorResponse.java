package com.capstone.interviewtracker.dto.Response;

import java.time.LocalDateTime;

/**
 * This class is used to send error details in response.
 */
public class ErrorResponse {

    private String message;
    private int status;
    private LocalDateTime timestamp;

    /**
     * Constructor to set error details.
     */
    public ErrorResponse(String message, int status, LocalDateTime timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    /**
     * Returns error message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns status code.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Returns time of error.
     */
    public LocalDateTime getTimeStamp() {
        return timestamp;
    }

}
