package com.shinu.smartrailwayops.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shinu.smartrailwayops.exception.PassengerNotFoundException;
import com.shinu.smartrailwayops.model.Passenger;
import com.shinu.smartrailwayops.repository.PassengerRepository;

// PassengerService handles business logic related to passengers
@Service
public class PassengerService {

    // Repository dependency (injected using constructor)
    private final PassengerRepository passengerRepository;

    // Constructor based dependency injection
    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    // Returns list of all passengers
    public List<Passenger> getAllPassengers() {
        return passengerRepository.getAll();
    }

    // Returns passenger by ID
    public Passenger getPassengerById(int id) {

        // Fetch passenger from repository
        Passenger passenger = passengerRepository.getById(id);

        // If passenger not found throw custom exception
        if (passenger == null) {
            throw new PassengerNotFoundException("Passenger not found with id: " + id);
        }

        return passenger;
    }

    // Adds a new passenger
    public void addPassenger(Passenger passenger) {

        // Check if passenger with same ID already exists
        if (passengerRepository.getById(passenger.getId()) != null) {

            // Throw HTTP 409 CONFLICT error
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Passenger already exists with id: " + passenger.getId());
        }

        // Save passenger to repository
        passengerRepository.save(passenger);
    }
}