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

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public List<User> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String role) {
        return userService.searchUsers(name, age, role);
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitUser(@RequestBody User user) {

        if (user.getName() == null || user.getName().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input - name is required");
        }

        if (user.getAge() <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input - age must be greater than 0");
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input - role is required");
        }

        userService.addUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User submitted successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable int id,
            @RequestParam(required = false) Boolean confirm) {
        if (confirm == null || !confirm) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Confirmation required");
        }

        boolean deleted = userService.deleteUser(id, true);

        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found or not deleted");
        }

        return ResponseEntity.ok("User deleted successfully");
    }

}
