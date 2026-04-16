package com.shinu.smartrailwayops.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.shinu.smartrailwayops.component.MessageFormatter;

@Service
public class MessageService {
    private final Map<String, MessageFormatter> formatterMap;

    public MessageService(Map<String, MessageFormatter> formatterMap) {
        this.formatterMap = formatterMap;
    }

    public String getMessage(String type) {
        MessageFormatter formatter = formatterMap.get(type);
        if (formatter == null) {
            return "Invalid message type";
        }

        return formatter.formatMessage();
    }
}
