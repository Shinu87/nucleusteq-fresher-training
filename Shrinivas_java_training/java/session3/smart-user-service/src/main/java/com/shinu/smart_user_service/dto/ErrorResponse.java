package com.shinu.smart_user_service.dto;

// This class is used to send error details in API response
// Instead of sending plain text we return structured JSON
public class ErrorResponse {

    // Error code
    private String code;

    // Error message describing what went wrong
    private String message;

    // Constructor to initialize error response
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // Getter for error code
    public String getCode() {
        return code;
    }

    // Getter for error message
    public String getMessage() {
        return message;
    }
}