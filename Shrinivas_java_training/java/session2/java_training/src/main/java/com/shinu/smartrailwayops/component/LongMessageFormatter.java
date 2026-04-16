package com.shinu.smartrailwayops.component;

import org.springframework.stereotype.Component;

// "LONG" is the name used to identify this formatter at runtime
@Component("LONG")
public class LongMessageFormatter implements MessageFormatter {

    // This method returns a detailed long message for the user
    @Override
    public String formatMessage() {
        // Returning a full confirmation message for ticket booking
        return "Your train ticket has been successfully confirmed. Have a safe journey!";
    }

}