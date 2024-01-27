package com.ticketty.tickettyapp.service.email;

import com.ticketty.tickettyapp.dto.email.MailCodeRequest;
import com.ticketty.tickettyapp.dto.email.MailCodeResponse;
import com.ticketty.tickettyapp.dto.email.MailVerifyRequest;
import com.ticketty.tickettyapp.dto.email.MailVerifyResponse;
import com.ticketty.tickettyapp.service.user.UserService;
import com.ticketty.tickettyapp.util.Encryption;
import com.ticketty.tickettyapp.util.RedisUtil;
import com.ticketty.tickettyapp.repository.user.UserJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@RequiredArgsConstructor
@Service
public class MailService {
    private final UserJdbcRepository userJdbcRepository;
    private final RedisUtil redisUtil;
    private final JavaMailSender javaMailSender;
    private final Encryption encryption;


    @Value("${mail.sender-email}")
    private String senderEmail;

    public MailCodeResponse sendMail(MailCodeRequest request) {
        // 이미 가입된 email일 경우
        if (!userJdbcRepository.isUserNotExist(request.getEmail())) {
            return new MailCodeResponse("null", false, "DUPLICATION");
        }

        String code = generateRandomNumber();

        MimeMessage message = createMail(request.getEmail(), code);
        javaMailSender.send(message);

        // Redis에 데이터 저장
        redisUtil.setDataExpire(request.getEmail(), code, 60);
        System.out.println("token: "+ code);

        return new MailCodeResponse(code, true, "NULL");
    }

    private String generateRandomNumber() {
        return Integer.toString((int) (Math.random() * 90000) + 100000);
    }

    private MimeMessage createMail(String mail, String token) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "<h3>요청하신 인증 번호입니다.</h3>"
                    + "<h1>" + token + "</h1>"
                    + "<h3>감사합니다.</h3>";
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to create mail message.", e);
        }
        return message;
    }

    public MailVerifyResponse verifyMail(MailVerifyRequest request) {
        String redisAuthCode = redisUtil.getData(request.getEmail());

        if (redisAuthCode == null) {
            return new MailVerifyResponse(false, "EXPIRED");
        }

        boolean isEqual = redisAuthCode.equalsIgnoreCase(request.getCode());

        if (!isEqual) {
            return new MailVerifyResponse(false, "AUTHENTICATION_FAILED");
        }

        // 이메일 인증 성공
        return new MailVerifyResponse(true, "VERIFIED");
    }

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
