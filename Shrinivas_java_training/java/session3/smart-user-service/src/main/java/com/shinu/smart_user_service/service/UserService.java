package com.shinu.smart_user_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.shinu.smart_user_service.exception.InvalidUserException;
import com.shinu.smart_user_service.exception.UserNotFoundException;
import com.shinu.smart_user_service.model.User;
import com.shinu.smart_user_service.repository.UserRepository;

// Service layer contains all business logic
// It connects Controller and Repository
@Service
public class UserService {

    // Repository object
    private final UserRepository userRepository;

    // Constructor injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Search users based on optional filters
    // name and role : case-insensitive match
    // age : exact match
    public List<User> searchUsers(String name, Integer age, String role) {

        // get all users from repository
        List<User> users = userRepository.getAllUsers();

        // apply filtering using stream
        return users.stream().filter(user -> {

            // check name condition
            boolean matchesName = (name == null || user.getName().equalsIgnoreCase(name));

            // check age condition
            boolean matchesAge = (age == null || user.getAge() == age);

            // check role condition
            boolean matchesRole = (role == null || user.getRole().equalsIgnoreCase(role));

            // return only users matching all conditions
            return matchesName && matchesAge && matchesRole;
        })
                .collect(Collectors.toList());
    }

    // Delete user by ID with confirmation
    public void deleteUser(int id, boolean confirm) {

        // check if confirmation is provided
        if (!confirm) {
            throw new InvalidUserException("Confirmation required");
        }

        // get all users
        List<User> users = userRepository.getAllUsers();

        // check if user exists
        boolean exists = users.stream()
                .anyMatch(user -> user.getId() == id);

        // if not found throw exception
        if (!exists) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        // delete user from repository
        userRepository.deleteUser(id);
    }

    // Add new user
    public void addUser(User user) {

        // validate user before adding
        validateUser(user);

        // store user in repository
        userRepository.addUser(user);
    }

    // Validate user data before adding
    public void validateUser(User user) {

        // check if user object is null
        if (user == null) {
            throw new InvalidUserException("User data cannot be null");
        }

        // validate name
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new InvalidUserException("Name cannot be empty");
        }

        // validate age
        if (user.getAge() <= 0) {
            throw new InvalidUserException("Age must be greater than 0");
        }

        // validate role
        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new InvalidUserException("Role cannot be empty");
        }

        // check duplicate ID
        boolean exists = userRepository.getAllUsers()
                .stream()
                .anyMatch(u -> u.getId() == user.getId());

        if (exists) {
            throw new InvalidUserException("User with this ID already exists");
        }
    }
}