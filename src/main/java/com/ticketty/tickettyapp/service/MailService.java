package com.ticketty.tickettyapp.service;

import com.ticketty.tickettyapp.util.EmailValidator;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.repository.UserEntityRepository;
import com.ticketty.tickettyapp.util.PasswordValidator;
import com.ticketty.tickettyapp.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailService {

    private final UserEntityRepository userEntityRepository;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final RedisUtil redisUtil;
    private final JavaMailSender javaMailSender;

    @Value("${mail.sender-email}")
    private String senderEmail;

    public String sendMail(String email) {

        // 이메일 validation
        if (!emailValidator.test(email)) {
            throw new TickettyAppApplicationException(ErrorCode.EMAIL_VALIDATION, String.format("%s, Email validation failed", email));
        }

        // 이미 가입된 email일 경우
        userEntityRepository.findByEmail(email).ifPresent(it -> {
            throw new TickettyAppApplicationException(ErrorCode.DUPLICATED_EMAIL, String.format("%s is duplicated", email));
        });

        String code = generateRandomNumber();

        MimeMessage message = createMail(email, code);
        javaMailSender.send(message);

        // Redis에 데이터 저장
        redisUtil.setDataExpire(email, code, 60);
        System.out.println("code: "+ code);

        return code;
    }

    private String generateRandomNumber() {
        return Integer.toString((int) (Math.random() * 90000) + 100000);
    }

    private MimeMessage createMail(String email, String code) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("이메일 인증");
            String body = "<h3>요청하신 인증 번호입니다.</h3>"
                    + "<h1>" + code + "</h1>"
                    + "<h3>감사합니다.</h3>";
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to create mail message.", e);
        }
        return message;
    }

    public boolean verifyMail(String email, String password, String code) {
        // 비밀번호 validation 실패
        if (!passwordValidator.test(password)) {
            throw new TickettyAppApplicationException(ErrorCode.PASSWORD_VALIDATION, String.format("%s, Password validation failed", password));
        }

        String CodeStoredInRedis = redisUtil.getData(email);

        // 코드가 만료된 경우
        if (CodeStoredInRedis == null) {
            throw new TickettyAppApplicationException(ErrorCode.EXPIRED_CODE, String.format("%s, Expired code", email));
        }

        // 코드 인증 실패 (입력한 코드와 메일로 발송된 코드가 다른 경우)
        boolean isEqual = CodeStoredInRedis.equalsIgnoreCase(code);
        if (!isEqual) {
            throw new TickettyAppApplicationException(ErrorCode.AUTHENTICATION_FAILED, String.format("%s, Email authentication failed", email));
        }

        // 이메일 인증 성공
        return true;
    }


}
