package com.ticketty.tickettyapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisUtil {

    private static final String EMAIL_TOKEN_PREFIX = "emailVerificationCode:";
    private static final String REFRESH_TOKEN_PREFIX = "jwtRefreshToken:";
    private static final String BLACKLISTED_ACCESS_TOKEN_PREFIX = "blacklistedAccessToken:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 이메일 인증 코드 저장
    public void saveEmailToken(String email, String verificationCode, long expirationTimeMs) {
        String emailTokenKey = EMAIL_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(emailTokenKey, verificationCode);
        redisTemplate.expire(emailTokenKey, expirationTimeMs, TimeUnit.MILLISECONDS);
    }

    // refresh token 저장
    public void saveJwtToken(String email, String refreshToken, long expirationTimeMs) {
        String jwtTokenKey = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(jwtTokenKey, refreshToken);
        redisTemplate.expire(jwtTokenKey, expirationTimeMs, TimeUnit.MILLISECONDS);
    }

    // 이메일 인증 코드 가져오기
    public String getEmailToken(String email) {
        return redisTemplate.opsForValue().get(EMAIL_TOKEN_PREFIX + email);
    }

    // refresh token 가져오기
    public String getJwtToken(String email) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + email);
    }

    // 이메일 인증 코드 삭제
    public void deleteEmailToken(String email) {
        redisTemplate.delete(EMAIL_TOKEN_PREFIX + email);
    }

    // refresh token 삭제
    public void deleteRefreshTokenInRedis(String email) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + email);
    }


    // access token 블랙리스트로 저장
    public void saveBlacklistedAccessToken(String accessToken) {
//        String blacklistedTokenKey = BLACKLISTED_ACCESS_TOKEN_PREFIX + accessToken;
        String blacklistedTokenKey = BLACKLISTED_ACCESS_TOKEN_PREFIX + accessToken;
        redisTemplate.opsForValue().set(blacklistedTokenKey, "BLACKLISTED");
    }

    // access token이 블랙리스트에 저장되어있는지 여부 확인
    public boolean isAccessTokenBlacklisted(String accessToken) {
        String blacklistedTokenKey = BLACKLISTED_ACCESS_TOKEN_PREFIX + accessToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(blacklistedTokenKey));
    }


}
