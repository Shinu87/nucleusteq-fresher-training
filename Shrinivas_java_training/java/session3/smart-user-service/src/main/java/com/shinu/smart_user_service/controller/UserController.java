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

        userService.addUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User submitted successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable int id,
            @RequestParam(required = false) Boolean confirm) {

        userService.deleteUser(id, confirm != null && confirm);

        return ResponseEntity.ok("User deleted successfully");
    }

}
