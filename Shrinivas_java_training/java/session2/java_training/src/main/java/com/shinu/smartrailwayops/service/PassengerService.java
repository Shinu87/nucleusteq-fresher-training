package com.shinu.smartrailwayops.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shinu.smartrailwayops.exception.PassengerNotFoundException;
import com.shinu.smartrailwayops.model.Passenger;
import com.shinu.smartrailwayops.repository.PassengerRepository;

@Service
public class PassengerService {
    private final PassengerRepository passengerRepository;

    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public List<Passenger> getAllPassengers() {
        return passengerRepository.getAll();
    }

    public Passenger getPassengerById(int id) {

        Passenger passenger = passengerRepository.getById(id);

        if (passenger == null) {
            throw new PassengerNotFoundException("Passenger not found with id: " + id);
        }

        return passenger;
    }

    public void addPassenger(Passenger passenger) {
        if (passengerRepository.getById(passenger.getId()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Passenger already exists with id: " + passenger.getId());
        }
        passengerRepository.save(passenger);
    }
}
