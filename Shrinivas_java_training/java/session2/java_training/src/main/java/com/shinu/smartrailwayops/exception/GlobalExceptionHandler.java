package com.shinu.smartrailwayops.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// This class handles exceptions globally for the application
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles PassengerNotFoundException
    @ExceptionHandler(PassengerNotFoundException.class)
    public Map<String, String> handleNotFound(PassengerNotFoundException ex) {

        // Creating a map to return error response
        Map<String, String> error = new HashMap<>();

        // Adding error message
        error.put("error", ex.getMessage());

        return error;
    }
}