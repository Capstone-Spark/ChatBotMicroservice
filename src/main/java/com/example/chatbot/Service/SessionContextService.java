package com.example.chatbot.Service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionContextService {
    private final Map<String, String> sessionContext = new ConcurrentHashMap<>();

    public void saveSessionData(String sessionId, String data) {
        sessionContext.put(sessionId, data);
    }

    public String getSessionData(String sessionId) {
        return sessionContext.getOrDefault(sessionId, "");
    }

    public void clearSession(String sessionId) {
        sessionContext.remove(sessionId);
    }
}
