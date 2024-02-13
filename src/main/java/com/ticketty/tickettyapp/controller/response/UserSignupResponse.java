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
    private UserRole role;

    public static UserSignupResponse fromUser(User user) {
        return new UserSignupResponse(
                user.getId(),
                user.getEmail(),
                user.getUserRole()
        );
    }
}
