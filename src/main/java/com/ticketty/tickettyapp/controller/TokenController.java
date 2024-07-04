package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.response.TokenResponse;
import com.ticketty.tickettyapp.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @GetMapping
    public Mono<TokenResponse> getToken() {
        return tokenService.getToken();
    }
}
