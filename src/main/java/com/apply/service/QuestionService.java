package com.apply.service;


import com.apply.entity.Question;
import java.util.List;
import java.util.Optional;

public interface QuestionService {
    Question saveQuestion(Question question);
    List<Question> getQuestionsByPlatform(String platform);
    Optional<Question> getQuestionByPlatformAndText(String platform, String question);
    List<Question> getAllQuestions();
    Question updateQuestion(Long id, Question questionDetails);
    void deleteQuestion(Long id);
}
