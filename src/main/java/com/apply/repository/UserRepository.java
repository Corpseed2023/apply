package com.apply.repository;

import com.apply.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByKeywordsContaining(String keyword);

    Optional<User> findByEmail(String email);
}
