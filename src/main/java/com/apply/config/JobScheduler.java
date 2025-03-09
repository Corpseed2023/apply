package com.apply.config;


import com.apply.entity.UserCredential;
import com.apply.repository.UserCredentialRepository;
import com.apply.serviceImpl.AutomationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JobScheduler {

    @Autowired
    private  UserCredentialRepository userCredentialRepository;
    @Autowired
    private  AutomationService automationService;

    @Scheduled(cron = "0 0 9 * * ?")
    public void automateJobApplications() {
        List<UserCredential> users = userCredentialRepository.findAll();

        for (UserCredential user : users) {
            for (String jobTitle : user.getJobTitles()) {
//                automationService.applyFor(user.getPlatform(), jobTitle);
            }
        }
    }
}
