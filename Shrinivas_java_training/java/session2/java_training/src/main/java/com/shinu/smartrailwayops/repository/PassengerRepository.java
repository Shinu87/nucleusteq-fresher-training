package com.shinu.smartrailwayops.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.shinu.smartrailwayops.model.Passenger;

@Repository
public class PassengerRepository {

    private final List<Passenger> passengers = new ArrayList<>();

    public PassengerRepository() {
        passengers.add(new Passenger(1, "Shinu", "shinu@mail.com"));
        passengers.add(new Passenger(2, "Alex", "alex@mail.com"));

        passengers.add(new Passenger(3, "Ravi Kumar", "ravi@gmail.com"));
        passengers.add(new Passenger(4, "Ananya Sharma", "ananya@gmail.com"));
        passengers.add(new Passenger(5, "Arjun Reddy", "arjun.reddy@gmail.com"));
        passengers.add(new Passenger(6, "Priya Verma", "priya.verma@gmail.com"));
        passengers.add(new Passenger(7, "Vikram Singh", "vikram.singh@gmail.com"));
        passengers.add(new Passenger(8, "Sneha Iyer", "sneha.iyer@gmail.com"));
        passengers.add(new Passenger(9, "Rahul Mehta", "rahul.mehta@gmail.com"));
        passengers.add(new Passenger(10, "Kavya Nair", "kavya.nair@gmail.com"));
    }

    public List<Passenger> getAll() {
        return passengers;
    }

    public Passenger getById(int id) {
        for (Passenger p : passengers) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public void save(Passenger passenger) {
        passengers.add(passenger);
    }

}
