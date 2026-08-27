package com.example.chatbot.Controller;

import com.example.chatbot.Model.ChatRequest;
import com.example.chatbot.Model.ChatResponse;
import com.example.chatbot.Service.ChatbotService;
import com.example.chatbot.Service.ProductService;
import com.example.chatbot.Service.SessionContextService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private SessionContextService sessionContextService;
    
    @PostMapping("/message")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest chatRequest) {
        String sessionId = chatRequest.getSessionId();
        String userMessage = chatRequest.getUserMessage();

        System.out.println("📩 User: " + userMessage + " (Session: " + sessionId + ")");

        List<Map<String, Object>> allProducts = productService.getAllProducts();
        Optional<Map<String, Object>> matchedProduct = productService.searchProduct(userMessage);

        String prompt;

        // ✔ CASE 1: Product found
        if (matchedProduct.isPresent()) {
            Map<String, Object> p = matchedProduct.get();
            prompt = """
    You are an e-commerce AI assistant. Use ONLY the following product data, never use your own knowledge.

    Product:
    Name: %s
    Price: %s
    Stock: %s
    Description: %s

    Customer question: %s

    Answer clearly using ONLY the above product data.
    """.formatted(
                p.get("name"),
                p.get("price"),
                p.get("stock"),
                p.get("description"),
                userMessage
            );
        }
        // ❌ CASE 2: Product not found, but user asked something product-related
        else if (userMessage.toLowerCase().contains("price") ||
                userMessage.toLowerCase().contains("stock") ||
                userMessage.toLowerCase().contains("details")) {

            prompt = """
    You are an e-commerce assistant.

    The customer asked about a product, but the product does NOT exist in our store.

    Customer question: %s

    Respond:
    "This product is not available in our store."
    """.formatted(userMessage);
        }
        // ❌ CASE 3: Not product-related
        else {
            prompt = """
    You are an e-commerce assistant.

    You MUST answer only product-related queries.

    If the customer asks anything unrelated to products, respond with:
    "I can only answer questions related to the products in our store."

    Customer message: %s
    """.formatted(userMessage);
        }

        String reply = chatbotService.getChatResponse(prompt);
        return ResponseEntity.ok(new ChatResponse(reply));
    }


    @PostMapping("/session/clear")
    public ResponseEntity<Void> clearSession(@RequestParam String sessionId) {
        sessionContextService.clearSession(sessionId);
        return ResponseEntity.ok().build();
    }
}
