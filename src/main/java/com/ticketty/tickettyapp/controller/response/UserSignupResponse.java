package com.ticketty.tickettyapp.controller.response;

import com.ticketty.tickettyapp.model.User;
import com.ticketty.tickettyapp.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignupResponse {

    private Integer id;
    private String email;
    private String nickname;
    private UserRole role;

    // User 객체를 UserSignupResponse 객체로 변환
    public static UserSignupResponse fromUser(User user) {
        return new UserSignupResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getUserRole()
        );
    }
}
