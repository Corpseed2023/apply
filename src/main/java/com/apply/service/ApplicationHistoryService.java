package com.apply.service;

import com.apply.entity.ApplicationHistory;

import java.util.List;
import java.util.Optional;

public interface ApplicationHistoryService {
    ApplicationHistory saveApplication(ApplicationHistory applicationHistory);
    List<ApplicationHistory> getApplicationsByPlatform(String platform);
    List<ApplicationHistory> getAllApplications();
    Optional<ApplicationHistory> getApplicationById(Long id);
    void deleteApplication(Long id);
}
