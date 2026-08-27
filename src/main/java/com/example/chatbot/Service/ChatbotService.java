package com.example.chatbot.Service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ChatbotService {
    
    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
        .baseUrl("https://generativelanguage.googleapis.com/v1beta")
        .build();

    public String getChatResponse(String prompt) {
        try {
            // ✅ CORRECT model from your list: "models/gemini-2.5-flash"
            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();
            part.put("text", prompt);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            
            requestBody.put("contents", contents);

            System.out.println("=== REQUEST BODY ===");
            System.out.println(requestBody.toString(2));
            System.out.println("=== END REQUEST ===");

            String response = webClient.post()
                .uri("/models/gemini-2.5-flash:generateContent?key=" + apiKey)  // ✅ FIXED MODEL
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody.toString())
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(15));

            System.out.println("=== GEMINI RAW RESPONSE ===");
            System.out.println(response);
            System.out.println("=== END RESPONSE ===");

            JSONObject json = new JSONObject(response);
            
            if (json.has("candidates") && json.getJSONArray("candidates").length() > 0) {
                JSONObject candidate = json.getJSONArray("candidates").getJSONObject(0);
                if (candidate.has("content")) {
                    JSONObject contentObj = candidate.getJSONObject("content");
                    if (contentObj.has("parts") && contentObj.getJSONArray("parts").length() > 0) {
                        String reply = contentObj.getJSONArray("parts").getJSONObject(0).getString("text");
                        return reply.trim();
                    }
                }
            }
            return "can't understand the question. Please try again!!! ";
            
        } catch (Exception e) {
            System.err.println("=== GEMINI ERROR ===");
            e.printStackTrace();
            return "Gemini API Error: " + e.getMessage();
        }
    }
}
