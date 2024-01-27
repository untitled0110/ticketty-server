package com.ticketty.tickettyapp.dto.email;

public class MailCodeResponse {
    private boolean success;
    private String error;
    private String code;

    public MailCodeResponse() {
    }

    public MailCodeResponse(String code, boolean success, String error) {
        this.success = success;
        this.error = error;
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }
}
