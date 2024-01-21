package com.ticketty.tickettyapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class helloWorldController {

    @GetMapping("/")
    public ResponseEntity<String> getState() {
        return ResponseEntity.ok("SERVER IS RUNNING");
    }

}