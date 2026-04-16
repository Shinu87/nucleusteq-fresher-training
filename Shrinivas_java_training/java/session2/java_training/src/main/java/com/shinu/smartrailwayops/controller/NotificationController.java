package com.shinu.smartrailwayops.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shinu.smartrailwayops.service.NotificationService;
import org.springframework.web.bind.annotation.PostMapping;

// This controller handles notification related API requests
@RestController
@RequestMapping("/notify")
public class NotificationController {

    // Service layer dependency (injected using constructor)
    private final NotificationService notificationService;

    // Constructor injection
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // POST API to trigger notification based on event type
    @PostMapping()
    public String postMethodName(@RequestParam String eventType) {

        // Calls service to generate notification message
        return notificationService.triggerNotification(eventType);
    }

}