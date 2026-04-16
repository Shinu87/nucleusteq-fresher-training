package com.shinu.smartrailwayops.exception;

// Custom exception for handling cases when passenger is not found
public class PassengerNotFoundException extends RuntimeException {

    // Constructor to pass custom error message
    public PassengerNotFoundException(String message) {
        super(message);
    }

}