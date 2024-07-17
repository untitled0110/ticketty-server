package com.ticketty.tickettyapp.model;

public enum WinnerStatus {
    BEFORE_REQUEST, //요청 전
    REQUEST_COMPLETED, //요청 완료
    PAYMENT_COMPLETED, //지급 완료
    WINNING_CANCELLED, //당첨금 회수
    UNKNOWN;
}