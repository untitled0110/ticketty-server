package com.ticketty.tickettyapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TickettyAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(TickettyAppApplication.class, args);
    }

}

