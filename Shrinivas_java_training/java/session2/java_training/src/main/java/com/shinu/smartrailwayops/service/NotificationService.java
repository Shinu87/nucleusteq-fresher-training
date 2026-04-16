package com.shinu.smartrailwayops.service;

import org.springframework.stereotype.Service;

import com.shinu.smartrailwayops.component.NotificationComponent;

// Service class to handle notification related business logic
@Service
public class NotificationService {

    // Dependency on NotificationComponent (injected via constructor)
    private final NotificationComponent notificationComponent;

    // Constructor injection
    public NotificationService(NotificationComponent notificationComponent) {
        this.notificationComponent = notificationComponent;
    }

    // Method to trigger notification based on event type
    public String triggerNotification(String eventType) {

        // Calls component to generate notification message
        return notificationComponent.sendNotification(eventType);
    }

}