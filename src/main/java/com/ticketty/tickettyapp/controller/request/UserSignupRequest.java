package com.ticketty.tickettyapp.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignupRequest {

    private String email;
    private String password;
    private String code;
}
