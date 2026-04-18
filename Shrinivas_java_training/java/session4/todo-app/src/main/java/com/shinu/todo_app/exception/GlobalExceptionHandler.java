package com.shinu.todo_app.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// This class handles exceptions globally in the application
// It prevents application from crashing and returns proper error responses

@RestControllerAdvice
public class GlobalExceptionHandler {

    // This method handles TodoNotFoundException
    // It returns a proper 404 NOT FOUND response with error message
    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTodoNotFound(TodoNotFoundException ex) {

        // creating response body for error message
        Map<String, Object> response = new HashMap<>();

        response.put("message", ex.getMessage()); // error message
        response.put("status", HttpStatus.NOT_FOUND.value()); // HTTP status code

        // returning response with 404 status
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}