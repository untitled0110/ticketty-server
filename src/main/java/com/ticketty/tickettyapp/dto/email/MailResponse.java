package com.ticketty.tickettyapp.dto.email;

public class MailResponse {
    private boolean success;
    private String error;

    public MailResponse() {
    }

    public MailResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}

