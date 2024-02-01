package com.ticketty.tickettyapp.controller.email;

import com.ticketty.tickettyapp.dto.email.MailCodeRequest;
import com.ticketty.tickettyapp.dto.email.MailCodeResponse;
import com.ticketty.tickettyapp.dto.email.MailVerifyRequest;
import com.ticketty.tickettyapp.dto.email.MailVerifyResponse;
import com.ticketty.tickettyapp.service.email.MailService;
import com.ticketty.tickettyapp.util.EmailValidator;
import com.ticketty.tickettyapp.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MailController {

    private final MailService mailService;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;

    @PostMapping("/email/codes")
    public ResponseEntity<MailCodeResponse> sendMail(@RequestBody MailCodeRequest request) {

        if (!emailValidator.test(request.getEmail())) {
            MailCodeResponse validationErrorResponse = new MailCodeResponse("null", false, "EMAIL_VALIDATION");
            return ResponseEntity.ok(validationErrorResponse);
        }

        MailCodeResponse response = mailService.sendMail(request);

        return ResponseEntity.ok(response);
    }

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
        MailVerifyResponse signUpResponse = mailService.signUpUser(request);
        if (signUpResponse.isSuccess()) {
            // 회원가입 성공 시
            return ResponseEntity.ok(signUpResponse);
        } else {
            // 회원가입 실패 시
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(signUpResponse);
        }
    }
}
