package com.shinu.smart_user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.shinu.smart_user_service.dto.ErrorResponse;

// This class handles exceptions globally for all controllers
// It helps to send proper error responses
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles InvalidUserException (400 Bad Request)
    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<Object> handleInvalidUser(InvalidUserException ex) {

        // Returning structured JSON response using ErrorResponse DTO
        return new ResponseEntity<>(
                new ErrorResponse("400", ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    // Handles UserNotFoundException (404 Not Found)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {

        // Returning simple message with 404 status
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}