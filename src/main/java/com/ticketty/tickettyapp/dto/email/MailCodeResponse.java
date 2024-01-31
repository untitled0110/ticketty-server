package com.ticketty.tickettyapp.dto.email;

public class MailCodeResponse {
    private boolean success;
    private String error;
    private Result result;

    public MailCodeResponse() {
    }

    public MailCodeResponse(String code, boolean success, String error) {
        this.success = success;
        this.error = error;
        this.result = new Result(code);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public Result getResult() {
        return result;
    }

    public static class Result {
        private String code;

        public Result(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
