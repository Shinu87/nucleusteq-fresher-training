package com.shinu.smart_user_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.shinu.smart_user_service.model.User;
import com.shinu.smart_user_service.service.UserService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// This class handles all user related API requests
@RestController
@RequestMapping("/users")
public class UserController {

    // Service layer object Spring will inject it
    private UserService userService;

    // Constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET API: Search users with filters
    @GetMapping("/search")
    public List<User> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String role) {
        return userService.searchUsers(name, age, role);
    }

    // POST API: Add new user
    // Takes JSON input from request body
    @PostMapping("/submit")
    public ResponseEntity<String> submitUser(@RequestBody User user) {

        // calling service to validate and store user
        userService.addUser(user);

        // returning success response with status 201 (Created)
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User submitted successfully");
    }

    // DELETE API: Delete user by ID
    // Requires confirmation parameter
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable int id, // getting id from URL
            @RequestParam(required = false) Boolean confirm) { // confirmation check

        // calling service to delete user
        userService.deleteUser(id, confirm != null && confirm);

        // returning success message
        return ResponseEntity.ok("User deleted successfully");
    }

}