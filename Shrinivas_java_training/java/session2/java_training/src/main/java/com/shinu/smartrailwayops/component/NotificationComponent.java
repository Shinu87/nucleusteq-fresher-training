package com.shinu.smartrailwayops.component;

import org.springframework.stereotype.Component;

@Component
public class NotificationComponent {
    public String sendNotification(String eventType) {

        if (eventType.equals("BOOKING")) {
            return "Ticket booked successfully!";
        }
        if (eventType.equals("CANCELLATION")) {
            return "Ticket cancelled successfully!";
        }

        return "General notification sent!";
    }

}
