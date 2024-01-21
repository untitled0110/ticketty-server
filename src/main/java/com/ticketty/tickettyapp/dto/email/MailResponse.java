package com.ticketty.tickettyapp.dto.email;

public class MailResponse {
    private boolean result;
    private String emailToken;

    public MailResponse(boolean result, String emailToken) {
        this.result = result;
        this.emailToken = emailToken;
    }

    public boolean isResult() {
        return result;
    }

    public String getEmailToken() {
        return emailToken;
    }
}

