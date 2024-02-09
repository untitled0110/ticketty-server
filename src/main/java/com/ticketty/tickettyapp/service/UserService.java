package com.ticketty.tickettyapp.service;

import com.ticketty.tickettyapp.controller.request.MailVerifyRequest;
import com.ticketty.tickettyapp.controller.response.MailVerifyResponse;
import com.ticketty.tickettyapp.repository.UserJdbcRepository;
import com.ticketty.tickettyapp.util.Encryption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserJdbcRepository userJdbcRepository;
    private final Encryption encryption;

    @Transactional
    public MailVerifyResponse signUpUser(MailVerifyRequest request) {
        // 이미 가입된 email일 경우
        if (!userJdbcRepository.isUserNotExist(request.getEmail())) {
            return new MailVerifyResponse(false, "DUPLICATION");
        }

        // 패스워드 암호화
        String hashedPassword = encryption.encode(request.getPassword());

        boolean signUpSuccess = userJdbcRepository.saveUser(request.getEmail(), hashedPassword);

        if (signUpSuccess) {
            // 회원가입 성공
            return new MailVerifyResponse(true, null);
        } else {
            // 회원가입 실패 시 오류 메시지 반환
            return new MailVerifyResponse(false, "SIGNUP_FAILED");
        }
    }

}
