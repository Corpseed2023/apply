package com.apply.repository;


import com.apply.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByPlatform(String platform);
    Optional<Question> findByPlatformAndQuestion(String platform, String question);
}
