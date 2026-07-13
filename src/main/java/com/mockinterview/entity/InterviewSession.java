package com.mockinterview.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;
    private String difficulty;
    private int averageScore;
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<InterviewAnswer> answers = new ArrayList<>();

    public InterviewSession() {}

    public InterviewSession(String role, String difficulty, int averageScore) {
        this.role = role;
        this.difficulty = difficulty;
        this.averageScore = averageScore;
        this.completedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getRole() { return role; }
    public String getDifficulty() { return difficulty; }
    public int getAverageScore() { return averageScore; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public List<InterviewAnswer> getAnswers() { return answers; }

    public void setAnswers(List<InterviewAnswer> answers) { this.answers = answers; }
}