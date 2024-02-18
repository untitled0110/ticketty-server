package com.ticketty.tickettyapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisUtil {

    private static final String EMAIL_TOKEN_PREFIX = "emailVerificationCode:";
    private static final String JWT_TOKEN_PREFIX = "jwtRefreshToken:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 이메일 인증 코드 저장
    public void saveEmailToken(String email, String verificationCode, long expirationTimeMs) {
        System.out.println("saveEmailToken 호출!!");
        String emailTokenKey = EMAIL_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(emailTokenKey, verificationCode);
        redisTemplate.expire(emailTokenKey, expirationTimeMs, TimeUnit.MILLISECONDS);
    }

    // refresh token 저장
    public void saveJwtToken(String email, String refreshToken, long expirationTimeMs) {
        String jwtTokenKey = JWT_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(jwtTokenKey, refreshToken);
        redisTemplate.expire(jwtTokenKey, expirationTimeMs, TimeUnit.MILLISECONDS);
    }

    // 이메일 인증 코드 가져오기
    public String getEmailToken(String email) {
        return redisTemplate.opsForValue().get(EMAIL_TOKEN_PREFIX + email);
    }

    // refresh token 가져오기
    public String getJwtToken(String email) {
        return redisTemplate.opsForValue().get(JWT_TOKEN_PREFIX + email);
    }

    // 이메일 인증 코드 삭제
    public void deleteEmailToken(String email) {
        redisTemplate.delete(EMAIL_TOKEN_PREFIX + email);
    }

    // refresh token 삭제
    public void deleteJwtToken(String email) {
        redisTemplate.delete(JWT_TOKEN_PREFIX + email);
    }

}
