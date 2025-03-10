package com.apply.serviceImpl;

import com.apply.entity.Platform;
import com.apply.entity.Question;
import com.apply.repository.PlatformRepository;
import com.apply.repository.QuestionRepository;
import com.apply.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private  QuestionRepository questionRepository;

    @Autowired
    private  PlatformRepository platformRepository;

    @Override
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getQuestionsByPlatform(String platformName) {
        Platform platform = platformRepository.findByName(platformName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Platform not found"));

        return questionRepository.findByPlatform(platform);
    }

    @Override
    public Optional<Question> getQuestionByPlatformAndText(String platformName, String questionText) {
        Platform platform = platformRepository.findByName(platformName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Platform not found"));

        return questionRepository.findByPlatformAndQuestion(platform, questionText);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question updateQuestion(Long id, Question questionDetails) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        // Update fields only if they are provided
        if (questionDetails.getPlatform() != null) {
            existingQuestion.setPlatform(questionDetails.getPlatform());
        }
        if (questionDetails.getQuestion() != null) {
            existingQuestion.setQuestion(questionDetails.getQuestion());
        }
        if (questionDetails.getAnswer() != null) {
            existingQuestion.setAnswer(questionDetails.getAnswer());
        }

        return questionRepository.save(existingQuestion);
    }

    @Override
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found");
        }
        questionRepository.deleteById(id);
    }
}
