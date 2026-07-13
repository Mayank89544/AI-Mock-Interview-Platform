package com.mockinterview.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class InterviewAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String question;

    @Column(length = 5000)
    private String answer;

    private int technicalAccuracy;
    private int depth;
    private int clarity;
    private int overallScore;

    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonIgnore
    private InterviewSession session;

    public InterviewAnswer() {}

    public InterviewAnswer(String question, String answer, int technicalAccuracy, int depth, int clarity, int overallScore) {
        this.question = question;
        this.answer = answer;
        this.technicalAccuracy = technicalAccuracy;
        this.depth = depth;
        this.clarity = clarity;
        this.overallScore = overallScore;
    }

    public Long getId() { return id; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public int getTechnicalAccuracy() { return technicalAccuracy; }
    public int getDepth() { return depth; }
    public int getClarity() { return clarity; }
    public int getOverallScore() { return overallScore; }
    public InterviewSession getSession() { return session; }

    public void setSession(InterviewSession session) { this.session = session; }
}