package com.mockinterview.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.genai.Client;
import com.google.genai.types.*;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;

    // The Client reads GOOGLE_API_KEY from the environment automatically
    public GeminiService() {
        this.client = new Client();
    }

    public String generateQuestions(String role, String difficulty) {
        // Build the user prompt with the role and difficulty
        String prompt = "Generate exactly 5 technical interview questions for a " + difficulty
                + " level " + role + " position. The questions should test core concepts, "
                + "problem-solving ability, and practical knowledge relevant to the role.";

        // System instruction tells Gemini how to behave
        Content systemInstruction = Content.fromParts(
                Part.fromText("You are a senior technical interviewer at a top tech company. "
                        + "Generate challenging but fair interview questions. "
                        + "Return your response as a JSON object with a 'questions' array "
                        + "where each item has 'id' (number 1-5), 'question' (the question text), "
                        + "and 'topic' (the technical topic being tested).")
        );



        Schema questionSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(ImmutableMap.of(
                        "id", Schema.builder().type(Type.Known.INTEGER).build(),
                        "question", Schema.builder().type(Type.Known.STRING).build(),
                        "topic", Schema.builder().type(Type.Known.STRING).build()
                ))
                .required(ImmutableList.of("id", "question", "topic"))
                .build();

        Schema schema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(ImmutableMap.of(
                        "questions", Schema.builder()
                                .type(Type.Known.ARRAY)
                                .items(questionSchema)
                                .build()
                ))
                .required(ImmutableList.of("questions"))
                .build();

        GenerateContentConfig config = GenerateContentConfig.builder()
                .thinkingConfig(ThinkingConfig.builder().thinkingBudget(0))
                .systemInstruction(systemInstruction)
                .responseMimeType("application/json")
                .responseSchema(schema)
                .build();

        // Call Gemini 2.5 Flash and return the JSON text
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash", prompt, config);

        return response.text();
    }

    public String evaluateAnswer(String question, String answer, String role) {
        // Build a prompt with the question and the candidate's answer
        String prompt = "Question: " + question + "\n\nCandidate's Answer: " + answer
                + "\n\nEvaluate this answer for a " + role + " interview.";

        // System instruction for evaluation behavior
        Content systemInstruction = Content.fromParts(
                Part.fromText("You are a senior technical interviewer evaluating a candidate's answer. "
                        + "Provide a detailed evaluation as JSON with: "
                        + "'technicalAccuracy' (score 1-10), "
                        + "'depth' (score 1-10), "
                        + "'clarity' (score 1-10), "
                        + "'overallScore' (average of the three scores, rounded), "
                        + "'strengths' (array of strings listing what was good), "
                        + "'improvements' (array of strings listing what could be better), "
                        + "'suggestion' (a brief tip for improvement).")
        );

        Schema evalSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(ImmutableMap.of(
                        "technicalAccuracy", Schema.builder().type(Type.Known.INTEGER).build(),
                        "depth", Schema.builder().type(Type.Known.INTEGER).build(),
                        "clarity", Schema.builder().type(Type.Known.INTEGER).build(),
                        "overallScore", Schema.builder().type(Type.Known.INTEGER).build(),
                        "strengths", Schema.builder().type(Type.Known.ARRAY)
                                .items(Schema.builder().type(Type.Known.STRING).build()).build(),
                        "improvements", Schema.builder().type(Type.Known.ARRAY)
                                .items(Schema.builder().type(Type.Known.STRING).build()).build(),
                        "suggestion", Schema.builder().type(Type.Known.STRING).build()
                ))
                .required(ImmutableList.of(
                        "technicalAccuracy", "depth", "clarity", "overallScore",
                        "strengths", "improvements", "suggestion"
                ))
                .build();

        GenerateContentConfig config = GenerateContentConfig.builder()
                .thinkingConfig(ThinkingConfig.builder().thinkingBudget(0))
                .systemInstruction(systemInstruction)
                .responseMimeType("application/json")
                .responseSchema(evalSchema)
                .build();


        // Call Gemini and return the evaluation JSON
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash", prompt, config);

        return response.text();
    }
}