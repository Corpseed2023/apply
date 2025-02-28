package com.apply.controller;

import com.apply.entity.ApplicationHistory;
import com.apply.service.ApplicationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/application-history")
@RequiredArgsConstructor
public class ApplicationHistoryController {

    @Autowired
    private ApplicationHistoryService applicationHistoryService;

    @PostMapping("/save")
    public ResponseEntity<ApplicationHistory> saveApplication(@RequestBody ApplicationHistory applicationHistory) {
        ApplicationHistory savedApplication = applicationHistoryService.saveApplication(applicationHistory);
        return new ResponseEntity<>(savedApplication, HttpStatus.CREATED);
    }

    // Get all applications for a platform
    @GetMapping("/platform/{platform}")
    public ResponseEntity<List<ApplicationHistory>> getApplicationsByPlatform(@PathVariable String platform) {
        List<ApplicationHistory> applications = applicationHistoryService.getApplicationsByPlatform(platform);
        return ResponseEntity.ok(applications);
    }

    // Get a specific application by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationHistory> getApplicationById(@PathVariable Long id) {
        Optional<ApplicationHistory> application = applicationHistoryService.getApplicationById(id);
        return application.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all applications
    @GetMapping("/all")
    public ResponseEntity<List<ApplicationHistory>> getAllApplications() {
        return ResponseEntity.ok(applicationHistoryService.getAllApplications());
    }

    // Delete a job application history
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteApplication(@PathVariable Long id) {
        try {
            applicationHistoryService.deleteApplication(id);
            return ResponseEntity.ok("Application history deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
