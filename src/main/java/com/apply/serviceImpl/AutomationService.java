package com.apply.serviceImpl;

import com.apply.entity.UserCredential;
import com.apply.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AutomationService {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private NaukriAutomationService naukriAutomationService;

    @Autowired
    private LinkedInAutomationService linkedInAutomationService;

    public void applyForAllUsers() {
        List<UserCredential> users = userCredentialRepository.findAll();

        for (UserCredential user : users) {
            String platformName = user.getPlatform().getName().toLowerCase();
            Set<String> keywords = user.getUser().getKeywords(); // Fetch keywords

            System.out.println("🚀 Applying for user: " + user.getUsername() + " on platform: " + platformName);
            System.out.println("🔍 Keywords for search: " + keywords);

            // ✅ Call platform-specific method with keywords
            switch (platformName) {
                case "naukri":
                    naukriAutomationService.applyForNaukri(user, keywords);
                    break;
                case "linkedin":
                    linkedInAutomationService.applyForLinkedIn(user, keywords);
                    break;
                default:
                    System.err.println("❌ Unsupported platform: " + platformName);
            }
        }
    }
}
