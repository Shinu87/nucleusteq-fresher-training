package com.shinu.smart_user_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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

            return matchesAge && matchesRole && matchesName;

        })
                .collect(Collectors.toList());
    }

    public boolean deleteUser(int id, boolean confirm) {
        if (!confirm) {
            return false;
        }

        userRepository.deleteUser(id);
        return true;
    }

    public void addUser(User user) {

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new RuntimeException("Name cannot be empty");
        }

        userRepository.addUser(user);
    }
}
