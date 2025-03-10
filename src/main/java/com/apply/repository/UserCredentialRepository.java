package com.apply.repository;

import com.apply.entity.Platform;
import com.apply.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    Optional<UserCredential> findByPlatform(Platform platform);

    Optional<UserCredential> findByUsername(String username);
}
