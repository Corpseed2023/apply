package com.apply.service;

import com.apply.entity.UserCredential;

import java.util.List;
import java.util.Optional;

public interface UserCredentialService {
    UserCredential saveCredential(UserCredential userCredential);
    Optional<UserCredential> getCredentialByPlatform(String platform);
    Optional<UserCredential> getCredentialByUsername(String username);
    List<UserCredential> getAllCredentials();
    void deleteCredential(Long id);
}
