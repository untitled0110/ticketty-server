package com.ticketty.tickettyapp.controller.email;

import com.ticketty.tickettyapp.dto.email.MailCodeRequest;
import com.ticketty.tickettyapp.dto.email.MailCodeResponse;
import com.ticketty.tickettyapp.dto.email.MailVerifyRequest;
import com.ticketty.tickettyapp.dto.email.MailVerifyResponse;
import com.ticketty.tickettyapp.service.email.MailService;
import com.ticketty.tickettyapp.util.EmailValidator;
import com.ticketty.tickettyapp.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
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
            return ResponseEntity.badRequest().body(validationErrorResponse);
        }

        MailCodeResponse response = mailService.sendMail(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/user/signup")
    public ResponseEntity<MailVerifyResponse> verifyMailAndSignup(@RequestBody MailVerifyRequest request) {

        // 인증 실패 시 바로 반환
        MailVerifyResponse verificationResponse = mailService.verifyMail(request);
        if (!verificationResponse.isSuccess()) {
            return ResponseEntity.badRequest().body(verificationResponse);
        }

        // 비밀번호 밸리데이션
        if (!passwordValidator.test(request.getPassword())) {
            MailVerifyResponse verificationResponse2 = new MailVerifyResponse(false, "PASSWORD_VALIDATION");
            return ResponseEntity.badRequest().body(verificationResponse2);
        }

        // 이메일 인증 성공 시 회원가입 시도
        if ("VERIFIED".equals(verificationResponse.getError())) {
            MailVerifyResponse signUpResponse = mailService.signUpUser(request);

            if (signUpResponse.isSuccess()) {
                // 회원가입 성공 시
                return ResponseEntity.ok(signUpResponse);
            } else {
                // 회원가입 실패 시
                return ResponseEntity.badRequest().body(signUpResponse);
            }
        } else {
            // 기타 상황 처리
            return ResponseEntity.badRequest().body(new MailVerifyResponse(false, "Invalid verification status."));
        }
    }
}
