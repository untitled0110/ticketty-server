package com.ticketty.tickettyapp.service.email;

import com.ticketty.tickettyapp.dto.email.MailRequest;
import com.ticketty.tickettyapp.dto.email.MailResponse;
import com.ticketty.tickettyapp.util.RedisUtil;
import com.ticketty.tickettyapp.repository.user.UserJdbcRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {
    private final UserJdbcRepository userJdbcRepository;
    private final RedisUtil redisUtil;

    private final JavaMailSender javaMailSender;
    private static final String SENDER_EMAIL = "ticketty.dev@gmail.com";

    public MailService(UserJdbcRepository userJdbcRepository, JavaMailSender javaMailSender, RedisUtil redisUtil) {
        this.userJdbcRepository = userJdbcRepository;
        this.javaMailSender = javaMailSender;
        this.redisUtil = redisUtil;
    }

    public MailResponse sendMail(MailRequest request) {
        // 이미 가입된 email일 경우
        if (!userJdbcRepository.isUserNotExist(request.getEmail())) {
            return new MailResponse(false, "null");
        }

        String token = generateRandomNumber();

        MimeMessage message = createMail(request.getEmail(), token);
        javaMailSender.send(message);

        // Redis에 데이터 저장
        redisUtil.setDataExpire(request.getEmail(), token, 120);

        return new MailResponse(true, token);
    }

    private String generateRandomNumber() {
        return Integer.toString((int) (Math.random() * 90000) + 100000);
    }

    private MimeMessage createMail(String mail, String token) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(SENDER_EMAIL);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "<h3>요청하신 인증 번호입니다.</h3>"
                    + "<h1>" + token + "</h1>"
                    + "<h3>감사합니다.</h3>";
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return message;
    }
}
