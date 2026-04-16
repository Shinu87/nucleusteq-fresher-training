package com.shinu.smartrailwayops.component;

import org.springframework.stereotype.Component;

// This class is a Spring component for short message formatting
// "SHORT" is used as a key to identify this formatter at runtime
@Component("SHORT")
public class ShortMessageFormatter implements MessageFormatter {

    // This method returns a short confirmation message
    @Override
    public String formatMessage() {
        return "Ticket Confirmed";
    }
}