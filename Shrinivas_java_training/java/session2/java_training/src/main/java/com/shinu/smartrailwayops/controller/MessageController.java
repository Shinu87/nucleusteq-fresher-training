package com.shinu.smartrailwayops.controller;

import org.springframework.web.bind.annotation.RestController;

import com.shinu.smartrailwayops.service.MessageService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String getMessage(@RequestParam String type) {
        return messageService.getMessage(type);
    }

}
