package com.shinu.smartrailwayops.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
        return passengerRepository.getById(id);
    }

    public void addPassenger(Passenger passenger) {
        passengerRepository.save(passenger);
    }
}
