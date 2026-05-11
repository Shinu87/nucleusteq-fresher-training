package com.capstone.interviewtracker.exception.advice;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.capstone.interviewtracker.dto.Response.ErrorResponse;
import com.capstone.interviewtracker.exception.custom.BadRequestException;
import com.capstone.interviewtracker.exception.custom.ConflictException;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
import com.capstone.interviewtracker.exception.custom.UnauthorizedException;

/**
 * Global exception handler for custom exceptions.
 * Converts application exceptions into standard API error responses.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomGlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomGlobalExceptionHandler.class);

    /**
     * Handles ResourceNotFoundException and returns 404 response.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            final ResourceNotFoundException ex) {

        logger.warn("ResourceNotFoundException caught - message: {}", ex.getMessage());

        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles BadRequestException and returns 400 response.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            final BadRequestException ex) {

        logger.warn("BadRequestException caught - message: {}", ex.getMessage());

        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ConflictException and returns 409 response.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            final ConflictException ex) {

        logger.warn("ConflictException caught - message: {}", ex.getMessage());

        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Handles UnauthorizedException and returns 401 response.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            final UnauthorizedException ex) {

        logger.warn("UnauthorizedException caught - message: {}", ex.getMessage());

        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Builds a common error response structure.
     */
    private ResponseEntity<ErrorResponse> buildResponse(
            final String message,
            final HttpStatus status) {

        logger.debug("Building error response - status: {}, message: {}", status.value(), message);

        final ErrorResponse body = new ErrorResponse(message, status.value(), LocalDateTime.now());

        return new ResponseEntity<>(body, status);
    }
}