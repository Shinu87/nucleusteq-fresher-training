package com.shinu.smartrailwayops.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shinu.smartrailwayops.model.Passenger;
import com.shinu.smartrailwayops.service.PassengerService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/users")
public class PassengerController {
    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping
    public List<Passenger> getALL() {
        return passengerService.getAllPassengers();
    }

}
