package com.ticketty.tickettyapp.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class ChangeAccountRequest {
    @Pattern(regexp = "^[0-9]*$", message = "숫자만 입력 가능합니다.")
    private String accountNumber;

    @Pattern(regexp = "^[ㄱ-ㅎ가-힣]*$", message = "한국어만 입력 가능합니다.")
    private String bankName;

    @Pattern(regexp = "^[a-zA-Z가-힣]*$", message = "한국어와 영어만 입력 가능합니다.")
    private String accountHolder;
}
              