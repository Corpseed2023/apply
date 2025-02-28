package com.apply.serviceImpl;

import com.apply.entity.ApplicationHistory;
import com.apply.repository.ApplicationHistoryRepository;
import com.apply.service.ApplicationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationHistoryServiceImpl implements ApplicationHistoryService {

    @Autowired
    private  ApplicationHistoryRepository applicationHistoryRepository;

    @Override
    public ApplicationHistory saveApplication(ApplicationHistory applicationHistory) {
        return applicationHistoryRepository.save(applicationHistory);
    }

    @Override
    public List<ApplicationHistory> getApplicationsByPlatform(String platform) {
        return applicationHistoryRepository.findByPlatform(platform);
    }

    @Override
    public List<ApplicationHistory> getAllApplications() {
        return applicationHistoryRepository.findAll();
    }

    @Override
    public Optional<ApplicationHistory> getApplicationById(Long id) {
        return applicationHistoryRepository.findById(id);
    }

    @Override
    public void deleteApplication(Long id) {
        if (!applicationHistoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application history not found");
        }
        applicationHistoryRepository.deleteById(id);
    }
}
