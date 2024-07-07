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


    NULL_INPUT(HttpStatus.BAD_REQUEST,"입력값이 null이면 안됩니다."),
    MISSING_REQUIRED_PARAMETER(HttpStatus.BAD_REQUEST,"빈 값이 있으면 안됩니다."),

    EMAIL_VALIDATION(HttpStatus.OK, "Email validation failed"),
    PASSWORD_VALIDATION(HttpStatus.OK, "Password validation failed"),
    DUPLICATED_EMAIL(HttpStatus.OK, "Email is duplicated"),
    EXPIRED_CODE(HttpStatus.OK, "Code has expired"),
    EMAIL_AUTHENTICATION_FAILED(HttpStatus.OK, "Email authentication failed"),

    NICKNAME_VALIDATION(HttpStatus.OK, "Nickname validation failed"),
    DUPLICATED_NICKNAME(HttpStatus.OK, "Nickname is duplicated"),

    PHONE_VALIDATION(HttpStatus.OK, "Phone validation failed"),
    DUPLICATED_PHONE(HttpStatus.OK, "Phone is duplicated"),

    ACCOUNT_VALIDATION(HttpStatus.OK, "Account validation failed"),

//    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),
//    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Password is invalid"),

    USER_NOT_FOUND(HttpStatus.OK, "User not founded"),
    INVALID_PASSWORD(HttpStatus.OK, "Password is invalid"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

    MISSING_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "No Access token in header"),
    MISSING_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "No Refresh token in header"),

    EXPIRED_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "Expired Access token"),
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "Invalid Access token"),

    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Expired Refresh token"),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Invalid Refresh token"),
    ALREADY_LOGGED_OUT_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "Already logged out access token"),
//    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Refresh token contained in the header and refresh token stored in Redis do not match"),

    WINNER_NOT_FOUND(HttpStatus.OK, "No winner found today"),

    NOT_TICKET_ISSUE_TIME(HttpStatus.OK, "This is not the time to issue tickets."),

    ALREADY_REGISTERED_ACCOUNT(HttpStatus.OK, "Account information for this user is already registered with the same values."),
    DUPLICATED_ACCOUNT(HttpStatus.OK, "This account is already registered in database."),
    ;

    private HttpStatus status;
    private String message;
}
