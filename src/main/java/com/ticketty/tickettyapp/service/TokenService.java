package com.ticketty.tickettyapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketty.tickettyapp.controller.response.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Base64;

@Service
public class TokenService {

    private final WebClient webClient;

    @Value("${niceapi.client_id}")
    private String clientId;

    @Value("${niceapi.client_secret}")
    private String clientSecret;

    @Value("${niceapi.get_token_url}")
    private String getTokenUrl;

    public TokenService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<TokenResponse> getToken() {
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        return webClient.post()
                .uri(getTokenUrl)
                .header("Authorization", authHeader)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=client_credentials&scope=default")
                .retrieve()
                .bodyToMono(String.class) // String으로 받음
                .doOnNext(response -> System.out.println("API Response: " + response)) // 응답 출력
                .map(response -> {
                    // JSON 파싱
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        return objectMapper.readValue(response, TokenResponse.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to parse response", e);
                    }
                })
                .doOnError(WebClientResponseException.class, e -> {
                    System.err.println("Error response: " + e.getResponseBodyAsString());
                });
    }
}