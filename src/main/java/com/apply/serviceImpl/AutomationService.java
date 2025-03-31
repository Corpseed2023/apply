package com.apply.serviceImpl;

import com.apply.entity.Platform;
import com.apply.entity.User;
import com.apply.entity.UserCredential;
import com.apply.repository.PlatformRepository;
import com.apply.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AutomationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private NaukriAutomationService naukriAutomationService;

    public void applyForAllUsers() {
        System.out.println("👤 [Automation] Starting job application automation...");

        String email = "kaushuthakur610@gmail.com";

        // Get or create user
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setName("Kaushlendra");
            newUser.setEmail(email);
            return userRepository.save(newUser);
        });

        // 🧠 Ensure keywords are always set (even if user was fetched)
        user.setKeywords(Set.of("Java Developer"));

        System.out.println("📌 Keywords for user: " + user.getKeywords());

        // Get or create platform
        Platform platform = platformRepository.findByName("Naukri")
                .orElseGet(() -> platformRepository.save(new Platform("Naukri")));

        // Prepare credentials
        UserCredential testUser = new UserCredential();
        testUser.setUsername(email);
        testUser.setPassword("kaushu610");
        testUser.setPlatform(platform);
        testUser.setUser(user);

        // Launch automation
        naukriAutomationService.applyForNaukri(testUser, user.getKeywords());
    }
}
