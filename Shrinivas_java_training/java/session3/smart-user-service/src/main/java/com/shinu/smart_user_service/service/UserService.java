package com.shinu.smart_user_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.shinu.smart_user_service.exception.InvalidUserException;
import com.shinu.smart_user_service.exception.UserNotFoundException;
import com.shinu.smart_user_service.model.User;
import com.shinu.smart_user_service.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> searchUsers(String name, Integer age, String role) {
        List<User> users = userRepository.getAllUsers();

        return users.stream().filter(user -> {

            boolean matchesName = (name == null || user.getName().equalsIgnoreCase(name));

            boolean matchesAge = (age == null || user.getAge() == age);

            boolean matchesRole = (role == null || user.getRole().equalsIgnoreCase(role));

            return matchesName && matchesAge && matchesRole;
        })
                .collect(Collectors.toList());
    }

    public void deleteUser(int id, boolean confirm) {

        if (!confirm) {
            throw new InvalidUserException("Confirmation required");
        }

        List<User> users = userRepository.getAllUsers();

        boolean exists = users.stream()
                .anyMatch(user -> user.getId() == id);

        if (!exists) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteUser(id);
    }

    public void addUser(User user) {

        validateUser(user);

        userRepository.addUser(user);
    }

    public void validateUser(User user) {

        if (user == null) {
            throw new InvalidUserException("User data cannot be null");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new InvalidUserException("Name cannot be empty");
        }

        if (user.getAge() <= 0) {
            throw new InvalidUserException("Age must be greater than 0");
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new InvalidUserException("Role cannot be empty");
        }

        boolean exists = userRepository.getAllUsers()
                .stream()
                .anyMatch(u -> u.getId() == user.getId());

        if (exists) {
            throw new InvalidUserException("User with this ID already exists");
        }
    }
}
