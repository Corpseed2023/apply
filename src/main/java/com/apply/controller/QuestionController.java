package com.apply.controller;

import com.apply.entity.Question;
import com.apply.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // Save a new question
    @PostMapping("/save")
    public ResponseEntity<Question> saveQuestion(@RequestBody Question question) {
        Question savedQuestion = questionService.saveQuestion(question);
        return new ResponseEntity<>(savedQuestion, HttpStatus.CREATED);
    }

    // Get questions by platform
    @GetMapping("/platform/{platform}")
    public ResponseEntity<List<Question>> getQuestionsByPlatform(@PathVariable String platform) {
        List<Question> questions = questionService.getQuestionsByPlatform(platform);
        return ResponseEntity.ok(questions);
    }

    // Get all questions
    @GetMapping("/all")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/find")
    public ResponseEntity<Question> getQuestionByPlatformAndText(@RequestParam String platform, @RequestParam String question) {
        Optional<Question> foundQuestion = questionService.getQuestionByPlatformAndText(platform, question);
        return foundQuestion.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a question
    @PutMapping("/update/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody Question questionDetails) {
        Question updatedQuestion = questionService.updateQuestion(id, questionDetails);
        return ResponseEntity.ok(updatedQuestion);
    }

    // Delete a question
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok("Question deleted successfully!");
    }
}
