package com.shinu.smartrailwayops.service;

import org.springframework.stereotype.Service;

import com.shinu.smartrailwayops.component.NotificationComponent;

@Service
public class NotificationService {
    private final NotificationComponent notificationComponent;

    public NotificationService(NotificationComponent notificationComponent) {
        this.notificationComponent = notificationComponent;
    }

    public String triggerNotification(String eventType) {
        return notificationComponent.sendNotification(eventType);
    }

}
