package com.example.chatbot.Model;

import jakarta.validation.constraints.NotBlank;

public class ChatRequest {
    @NotBlank
    private String sessionId;
    @NotBlank
    private String userMessage;

    // Constructors
    public ChatRequest() {}
    
    public ChatRequest(String sessionId, String userMessage) {
        this.sessionId = sessionId;
        this.userMessage = userMessage;
    }

    // Getters & Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }
}
