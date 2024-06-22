package com.ticketty.tickettyapp.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;


@Getter
@NoArgsConstructor
public class ChangeEmojiRequest {
    @NotNull(message = "이모지를 입력해 주세요.")
//    @Pattern(regexp = "^[\u2600-\u26FF\u2700-\u27BF]$", message = "유효한 이모지여야 합니다.")
    private String emoji;
}
