package com.apply.controller;

import com.apply.serviceImpl.AutomationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/automation")
@RequiredArgsConstructor
public class AutomationController {

    @Autowired
    private AutomationService automationService;

//    @PostMapping("/apply")
//   public ResponseEntity<String> applyFor(@RequestParam String platform, @RequestParam String jobTitle) {
////        String result = automationService.applyFor(platform, jobTitle);
//        return ResponseEntity.ok(result);
//    }


}
