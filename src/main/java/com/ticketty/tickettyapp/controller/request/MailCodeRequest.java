package com.ticketty.tickettyapp.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MailCodeRequest {

    private String email;

    public MailCodeRequest() {
    }
}
