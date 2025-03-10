package com.apply.serviceImpl;

import com.apply.entity.Platform;
import com.apply.entity.UserCredential;
import com.apply.repository.PlatformRepository;
import com.apply.repository.UserCredentialRepository;
import com.apply.service.UserCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCredentialServiceImpl implements UserCredentialService {

    @Autowired
    private  UserCredentialRepository userCredentialRepository;

    @Autowired
    private  PlatformRepository platformRepository;

    @Override
    public UserCredential saveCredential(UserCredential userCredential) {
        return userCredentialRepository.save(userCredential);
    }

    @Override
    public Optional<UserCredential> getCredentialByPlatform(String platformName) {
        Platform platform = platformRepository.findByName(platformName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Platform not found"));

        return userCredentialRepository.findByPlatform(platform);
    }

    @Override
    public Optional<UserCredential> getCredentialByUsername(String username) {
        return userCredentialRepository.findByUsername(username);
    }

    @Override
    public List<UserCredential> getAllCredentials() {
        return userCredentialRepository.findAll();
    }

    @Override
    public void deleteCredential(Long id) {
        if (!userCredentialRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User credential not found");
        }
        userCredentialRepository.deleteById(id);
    }
}
