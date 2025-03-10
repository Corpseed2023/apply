package com.apply.controller;

import com.apply.dto.request.UserCredentialRequest;
import com.apply.entity.Platform;
import com.apply.entity.UserCredential;
import com.apply.repository.PlatformRepository;
import com.apply.service.UserCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/credentials")
@RequiredArgsConstructor
public class UserCredentialController {

    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private PlatformRepository platformRepository;

    // Save a new credential
    @PostMapping("/save")
    public ResponseEntity<UserCredential> saveCredential(@Valid @RequestBody UserCredentialRequest request) {
        Platform platform = platformRepository.findByName(request.getPlatform())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Platform not found"));

        UserCredential userCredential = new UserCredential();
        userCredential.setPlatform(platform);
        userCredential.setUsername(request.getUsername());
        userCredential.setPassword(request.getPassword());

        UserCredential savedCredential = userCredentialService.saveCredential(userCredential);
        return ResponseEntity.ok(savedCredential);
    }

    // Get credential by platform
    @GetMapping("/platform/{platform}")
    public ResponseEntity<UserCredential> getCredentialByPlatform(@PathVariable String platform) {
        Optional<UserCredential> credential = userCredentialService.getCredentialByPlatform(platform);
        return credential.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get credential by username
    @GetMapping("/username/{username}")
    public ResponseEntity<UserCredential> getCredentialByUsername(@PathVariable String username) {
        Optional<UserCredential> credential = userCredentialService.getCredentialByUsername(username);
        return credential.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all credentials
    @GetMapping("/all")
    public ResponseEntity<List<UserCredential>> getAllCredentials() {
        return ResponseEntity.ok(userCredentialService.getAllCredentials());
    }

    // Delete a credential by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCredential(@PathVariable Long id) {
        userCredentialService.deleteCredential(id);
        return ResponseEntity.ok("Credential deleted successfully!");
    }
}
