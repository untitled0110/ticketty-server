package com.ticketty.tickettyapp.controller.response;

import com.ticketty.tickettyapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageResponse {

    private String email;
    private String nickname;
    private String phone;
    private String accountNumber;
    private String bankName;

    // User 객체를 MyPageResponse 객체로 변환
    public static MyPageResponse fromUser(User user) {
        return new MyPageResponse(
                user.getEmail(),
                user.getNickname(),
                user.getPhone(),
                user.getAccountNumber(),
                user.getBankName()
        );
    }
}