package com.shinu.smartrailwayops.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.shinu.smartrailwayops.component.MessageFormatter;

// Service class to handle message related business logic
@Service
public class MessageService {

    // Map to store all MessageFormatter components for SHORT and LONG
    private final Map<String, MessageFormatter> formatterMap;

    // Constructor injection of all formatter components
    public MessageService(Map<String, MessageFormatter> formatterMap) {
        this.formatterMap = formatterMap;
    }

    // Method to get message based on type
    public String getMessage(String type) {

        // Fetch formatter based on type SHORT or LONG
        MessageFormatter formatter = formatterMap.get(type);

        // If no formatter found for given type
        if (formatter == null) {
            return "Invalid message type";
        }

        // Return formatted message
        return formatter.formatMessage();
    }
}