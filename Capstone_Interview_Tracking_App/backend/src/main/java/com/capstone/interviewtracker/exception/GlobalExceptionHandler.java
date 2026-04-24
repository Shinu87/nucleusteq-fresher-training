package com.capstone.interviewtracker.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.capstone.interviewtracker.dto.Response.ErrorResponse;

/**
 * Handles all exceptions globally in the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Runs when resource like user is not found.
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
                return new ResponseEntity<>(
                                new ErrorResponse(
                                                ex.getMessage(),
                                                HttpStatus.NOT_FOUND.value(),
                                                LocalDateTime.now()),
                                HttpStatus.NOT_FOUND);
        }

        /**
         * Handles normal runtime errors in the app.
         */
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<?> handleRuntime(RuntimeException ex) {
                return new ResponseEntity<>(
                                new ErrorResponse(
                                                ex.getMessage(),
                                                HttpStatus.BAD_REQUEST.value(),
                                                LocalDateTime.now()),
                                HttpStatus.BAD_REQUEST);
        }

        /**
         * Runs when login details are wrong.
         */
        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {
                return new ResponseEntity<>(
                                new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value(),
                                                java.time.LocalDateTime.now()),
                                HttpStatus.UNAUTHORIZED);
        }

}