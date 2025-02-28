package com.apply.serviceImpl;

import com.apply.entity.Question;
import com.apply.repository.QuestionRepository;
import com.apply.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private  QuestionRepository questionRepository;

    @Override
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getQuestionsByPlatform(String platform) {
        return questionRepository.findByPlatform(platform);
    }

    @Override
    public Optional<Question> getQuestionByPlatformAndText(String platform, String question) {
        return questionRepository.findByPlatformAndQuestion(platform, question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question updateQuestion(Long id, Question questionDetails) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));

        question.setPlatform(questionDetails.getPlatform());
        question.setQuestion(questionDetails.getQuestion());
        question.setAnswer(questionDetails.getAnswer());

        return questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new NoSuchElementException("Question not found");
        }
        questionRepository.deleteById(id);
    }
}
