package com.shinu.smartrailwayops.component;

import org.springframework.stereotype.Component;

@Component("LONG")
public class LongMessageFormatter implements MessageFormatter {

    @Override
    public String formatMessage() {
        return "Your train ticket has been successfully confirmed. Have a safe journey!";
    }

}
