package com.shinu.smartrailwayops.component;

import org.springframework.stereotype.Component;

// This class is a Spring component used to handle notification logic
// It generates messages based on event type
@Component
public class NotificationComponent {

    // This method returns a notification message based on event type
    public String sendNotification(String eventType) {

        // If event is booking
        if (eventType.equals("BOOKING")) {
            return "Ticket booked successfully!";
        }

        // If event is cancellation
        if (eventType.equals("CANCELLATION")) {
            return "Ticket cancelled successfully!";
        }

        // Default message for other events
        return "General notification sent!";
    }

}