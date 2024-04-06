package com.ticketty.tickettyapp.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class ChangeNicknameRequest {
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]{4,16}$", message = "닉네임은 특수문자를 제외한 4~16자리여야 합니다.")
    private String newNickname;
}
