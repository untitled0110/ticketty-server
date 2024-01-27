package com.ticketty.tickettyapp.dto.email;

public class MailVerifyResponse {

    private boolean success;
    private String error;

    public MailVerifyResponse() {
    }

    public MailVerifyResponse(boolean success, String error) {
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
