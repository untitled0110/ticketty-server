package com.ticketty.tickettyapp.controller;

import com.ticketty.tickettyapp.controller.request.MailVerifyRequest;
import com.ticketty.tickettyapp.controller.response.MailVerifyResponse;
import com.ticketty.tickettyapp.service.MailService;
import com.ticketty.tickettyapp.service.UserService;
import com.ticketty.tickettyapp.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final MailService mailService;
    private final UserService userService;
    private final PasswordValidator passwordValidator;

    @PostMapping("/user/signup")
    public ResponseEntity<MailVerifyResponse> verifyMailAndSignup(@RequestBody MailVerifyRequest request) {

        // 비밀번호 밸리데이션
        if (!passwordValidator.test(request.getPassword())) {
            MailVerifyResponse passwordValidateResponse = new MailVerifyResponse(false, "PASSWORD_VALIDATION");
            return ResponseEntity.ok(passwordValidateResponse);
        }

        // 인증 실패 시
        MailVerifyResponse verificationResponse = mailService.verifyMail(request);
        if (!verificationResponse.isSuccess()) {
            return ResponseEntity.ok(verificationResponse);
        }

        // 이메일 인증 성공 시 회원가입 시도
        MailVerifyResponse signUpResponse = userService.signUpUser(request);

        if (signUpResponse.isSuccess() || "DUPLICATION".equals(signUpResponse.getError())) {
            // 회원가입 성공 시
            return ResponseEntity.ok(signUpResponse);
        } else {
            // 회원가입 실패 시
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(signUpResponse);
        }
    }

}
