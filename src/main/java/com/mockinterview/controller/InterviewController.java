package com.mockinterview.controller;

import com.mockinterview.entity.InterviewAnswer;
import com.mockinterview.entity.InterviewSession;
import com.mockinterview.repository.InterviewSessionRepository;
import com.mockinterview.service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final GeminiService geminiService;
    private final InterviewSessionRepository sessionRepository;

    // Constructor now injects both the AI service and the database repository
    public InterviewController(GeminiService geminiService, InterviewSessionRepository sessionRepository) {
        this.geminiService = geminiService;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startInterview(@RequestBody Map<String, String> request) {
        String role = request.get("role");
        String difficulty = request.get("difficulty");
        String questions = geminiService.generateQuestions(role, difficulty);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/evaluate")
    public ResponseEntity<String> evaluateAnswer(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = request.get("answer");
        String role = request.get("role");
        String evaluation = geminiService.evaluateAnswer(question, answer, role);
        return ResponseEntity.ok(evaluation);
    }

    // Saves a completed interview session with all answers to the database
    @PostMapping("/save")
    public ResponseEntity<InterviewSession> saveSession(@RequestBody Map<String, Object> request) {
        String role = (String) request.get("role");
        String difficulty = (String) request.get("difficulty");
        List<Map<String, Object>> answers = (List<Map<String, Object>>) request.get("answers");

        int totalScore = 0;
        List<InterviewAnswer> answerEntities = new ArrayList<>();

        for (Map<String, Object> a : answers) {
            int score = ((Number) a.get("overallScore")).intValue();
            totalScore += score;
            InterviewAnswer entity = new InterviewAnswer(
                    (String) a.get("question"),
                    (String) a.get("answer"),
                    ((Number) a.get("technicalAccuracy")).intValue(),
                    ((Number) a.get("depth")).intValue(),
                    ((Number) a.get("clarity")).intValue(),
                    score
            );
            answerEntities.add(entity);
        }

        int avgScore = Math.round((float) totalScore / answers.size());
        InterviewSession session = new InterviewSession(role, difficulty, avgScore);

        for (InterviewAnswer answer : answerEntities) {
            answer.setSession(session);
        }
        session.setAnswers(answerEntities);

        sessionRepository.save(session);
        return ResponseEntity.ok(session);
    }

    // Returns all past interview sessions ordered by most recent first
    @GetMapping("/history")
    public ResponseEntity<List<InterviewSession>> getHistory() {
        return ResponseEntity.ok(sessionRepository.findAllByOrderByCompletedAtDesc());
    }
}