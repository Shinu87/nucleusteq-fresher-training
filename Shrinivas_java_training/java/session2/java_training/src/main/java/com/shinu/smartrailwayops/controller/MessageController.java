package com.shinu.smartrailwayops.controller;

import org.springframework.web.bind.annotation.RestController;

import com.shinu.smartrailwayops.service.MessageService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// This controller handles message related API requests
@RestController
@RequestMapping("/message")
public class MessageController {

    // Service layer dependency (injected using constructor)
    private final MessageService messageService;

    // Constructor injection
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // API to get message based on type SHORT or LONG
    @GetMapping
    public String getMessage(@RequestParam String type) {

        // Calls service to get appropriate message
        return messageService.getMessage(type);
    }

}