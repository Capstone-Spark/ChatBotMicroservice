package com.example.chatbot.Service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final WebClient webClient = WebClient.create("http://localhost:8082");

    public List<Map<String, Object>> getAllProducts() {

        // Read full Page<Product> JSON as Map
        Map<String, Object> pageResponse = webClient.get()
                .uri("/api/v1/products")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        // Extract "content" which is the List<Product>
        return (List<Map<String, Object>>) pageResponse.get("content");
    }

    public Optional<Map<String, Object>> searchProduct(String userMessage) {
        List<Map<String, Object>> products = getAllProducts();

        return products.stream()
                .filter(p -> userMessage.toLowerCase().contains(
                        p.get("name").toString().toLowerCase()
                ))
                .findFirst();
    }
}
