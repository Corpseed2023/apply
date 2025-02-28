package com.apply.repository;

import com.apply.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findByPlatform(String platform);
    Optional<UserCredential> findByUsername(String username);
}
