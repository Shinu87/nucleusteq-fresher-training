package com.shinu.smartrailwayops.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shinu.smartrailwayops.service.NotificationService;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/notify")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping()
    public String postMethodName(@RequestParam String eventType) {
        return notificationService.triggerNotification(eventType);
    }

}
