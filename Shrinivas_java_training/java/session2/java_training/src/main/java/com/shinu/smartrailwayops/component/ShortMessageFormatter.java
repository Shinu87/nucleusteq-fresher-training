package com.shinu.smartrailwayops.component;

import org.springframework.stereotype.Component;

@Component("SHORT")
public class ShortMessageFormatter implements MessageFormatter {

    @Override
    public String formatMessage() {
        return "Ticket Confirmed";
    }
}
