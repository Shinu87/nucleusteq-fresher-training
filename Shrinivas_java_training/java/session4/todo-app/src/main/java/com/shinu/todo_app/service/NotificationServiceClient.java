package com.shinu.todo_app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// This is a dummy external service client
// It simulates sending notifications like email or SMS

@Service
public class NotificationServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceClient.class);

    // simulate notification sending
    public void sendTodoCreatedNotification(String title) {

        logger.info("Notification Service: Sending notification for Todo -> {}", title);

        // simulation delay or message

        logger.info("Notification sent successfully for Todo: {}", title);
    }
}