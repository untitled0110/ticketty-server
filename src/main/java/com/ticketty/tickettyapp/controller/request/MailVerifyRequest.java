package com.ticketty.tickettyapp.controller.request;

public class MailVerifyRequest {

    private String email;
    private String code;
    private String password;

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }

    public String getPassword() {
        return password;
    }
}
