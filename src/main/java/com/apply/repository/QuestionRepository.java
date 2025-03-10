package com.apply.repository;

import com.apply.entity.Platform;
import com.apply.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByPlatform(Platform platform); // Accepts Platform entity

    Optional<Question> findByPlatformAndQuestion(Platform platform, String question); // Corrected method
}
