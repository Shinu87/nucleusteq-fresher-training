package com.shinu.smart_user_service.repository;

import java.util.ArrayList;
import java.util.List;

import com.shinu.smart_user_service.model.User;

public class UserRepository {
    private final List<User> users = new ArrayList<>();

    public UserRepository() {
        users.add(new User(1, "Priya", 25, "USER"));
        users.add(new User(2, "Rahul", 30, "ADMIN"));
        users.add(new User(3, "Anjali", 30, "USER"));
        users.add(new User(4, "Kiran", 28, "USER"));
        users.add(new User(5, "Arjun", 35, "ADMIN"));
    }

}