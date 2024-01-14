package com.ticketty.tickettyapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class helloWorldController {

    @PostMapping("/hello/world")
    public String test(@RequestParam String name) {
        return "Hello "+ name;
    };
}
