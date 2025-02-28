package com.apply.serviceImpl;


import com.apply.entity.UserCredential;
import com.apply.repository.UserCredentialRepository;
import com.apply.service.UserCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCredentialServiceImpl implements UserCredentialService {

    @Autowired
    private  UserCredentialRepository userCredentialRepository;

    @Override
    public UserCredential saveCredential(UserCredential userCredential) {
        return userCredentialRepository.save(userCredential);
    }

    @Override
    public Optional<UserCredential> getCredentialByPlatform(String platform) {
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
        userCredentialRepository.deleteById(id);
    }
}
