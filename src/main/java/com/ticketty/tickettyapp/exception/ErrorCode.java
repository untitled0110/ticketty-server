package com.ticketty.tickettyapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

//    EMAIL_VALIDATION(HttpStatus.BAD_REQUEST, "Email validation failed"),
//    PASSWORD_VALIDATION(HttpStatus.BAD_REQUEST, "Password validation failed"),
//    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "Email is duplicated"),
//    EXPIRED_CODE(HttpStatus.BAD_REQUEST, "Code has expired"),
//    AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST, "Email authentication failed"),

    EMAIL_VALIDATION(HttpStatus.OK, "Email validation failed"),
    PASSWORD_VALIDATION(HttpStatus.OK, "Password validation failed"),
    DUPLICATED_EMAIL(HttpStatus.OK, "Email is duplicated"),
    EXPIRED_CODE(HttpStatus.OK, "Code has expired"),
    AUTHENTICATION_FAILED(HttpStatus.OK, "Email authentication failed"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Password is invalid"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),



    ;

    private HttpStatus status;
    private String message;
}
