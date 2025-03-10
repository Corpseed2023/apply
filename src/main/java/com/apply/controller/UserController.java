package com.apply.controller;

import com.apply.dto.request.UserRequestDTO;
import com.apply.entity.User;
import com.apply.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ Save a new user using DTO
    @PostMapping("/save")
    public ResponseEntity<User> saveUser(@Valid @RequestBody UserRequestDTO userRequest) {
        User user = userService.createUser(userRequest);
        return ResponseEntity.ok(user);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all users
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Update user keywords
    @PutMapping("/{id}/keywords")
    public ResponseEntity<User> updateUserKeywords(@PathVariable Long id, @RequestBody Set<String> keywords) {
        return ResponseEntity.ok(userService.updateUserKeywords(id, keywords));
    }
}
