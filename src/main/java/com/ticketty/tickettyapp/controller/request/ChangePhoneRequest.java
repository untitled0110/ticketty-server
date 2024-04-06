package com.ticketty.tickettyapp.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class ChangePhoneRequest {

    @Pattern(regexp = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})\\d{4}$", message = "올바른 핸드폰 번호 형식이 아닙니다.")
    private String phone;

}
