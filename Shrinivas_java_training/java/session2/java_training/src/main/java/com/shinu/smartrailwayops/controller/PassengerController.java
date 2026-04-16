package com.shinu.smartrailwayops.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shinu.smartrailwayops.model.Passenger;
import com.shinu.smartrailwayops.service.PassengerService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// This controller handles passenger related API requests
@RestController
@RequestMapping("/users")
public class PassengerController {

    // Service layer dependency (constructor injection)
    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    // GET API to fetch all passengers
    @GetMapping
    public List<Passenger> getALL() {
        return passengerService.getAllPassengers();
    }

    // POST API to create a new passenger
    @PostMapping
    public String createPassenger(@RequestBody Passenger passenger) {

        // Calls service layer to save passenger
        passengerService.addPassenger(passenger);
        return "Passenger Added Successfully";
    }

    // GET API to fetch passenger by ID
    @GetMapping("/{id}")
    public Passenger getPassenger(@PathVariable int id) {

        // Calls service layer to get passenger by ID
        return passengerService.getPassengerById(id);
    }

}