package com.ticketty.tickettyapp.service;

import com.ticketty.tickettyapp.controller.response.UserLoginResponse;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.model.User;
import com.ticketty.tickettyapp.model.entity.UserEntity;
import com.ticketty.tickettyapp.repository.UserEntityRepository;
import com.ticketty.tickettyapp.util.JwtTokenUtils;
import com.ticketty.tickettyapp.util.PasswordEncoder;
import com.ticketty.tickettyapp.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class UserService {

    @Value("${jwt.token.expired-time-ms.refresh}")
    private Long refreshTokenExpiredTimeMs;

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final JwtTokenUtils jwtTokenUtils;

    @Transactional
    public User signUpUser(String email, String password) {
        // 이미 가입된 email일 경우
        userEntityRepository.findByEmail(email).ifPresent(it -> {
            throw new TickettyAppApplicationException(ErrorCode.DUPLICATED_EMAIL, String.format("%s is duplicated", email));
        });

        // 회원가입 진행 = user를 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(email, passwordEncoder.encrypt(email, password)));
        return User.fromEntity(userEntity);
    }

    public UserLoginResponse login(String email, String password) {
        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByEmail(email).orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", email)));

        // 비밀번호 체크
        if (!passwordEncoder.matches(email, password, userEntity.getPassword())) {
            throw new TickettyAppApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String accessToken = jwtTokenUtils.generateAccessToken(email);
        String refreshToken = jwtTokenUtils.generateRefreshToken();

        Date expiration = jwtTokenUtils.extractExpiration(accessToken);
//        System.out.println("AccessToken expiration!!!!!"+ expiration);

        long accessTokenExpiration = expiration.getTime();

        // Redis에 refresh token 저장
        redisUtil.saveJwtToken(email, refreshToken, refreshTokenExpiredTimeMs);

        return new UserLoginResponse(accessToken, refreshToken, accessTokenExpiration);
    }

    public UserLoginResponse reissueAccessToken(HttpServletRequest httpServletRequest) {
        // 헤더에서 토큰 추출
        String accessTokenInHeader = jwtTokenUtils.extractAccessToken(httpServletRequest);
        String refreshTokenInHeader = jwtTokenUtils.extractRefreshToken(httpServletRequest);

        // 헤더에 토큰이 없는 경우
        if (accessTokenInHeader == null) {
            throw new TickettyAppApplicationException(ErrorCode.MISSING_ACCESS_TOKEN);
        }
        if (refreshTokenInHeader == null) {
            throw new TickettyAppApplicationException(ErrorCode.MISSING_REFRESH_TOKEN);
        }

        // refresh token 토큰 유효성 검증
        jwtTokenUtils.validateRefreshToken(refreshTokenInHeader);

        String email = jwtTokenUtils.extractEmailFromExpiredAccessToken(accessTokenInHeader);
        // email로 회원가입 여부 체크
        userEntityRepository.findByEmail(email).orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", email)));

        // refresh token이 Redis에 있는지 확인 후 헤더의 refresh token과 비교
        String refreshTokenStoredInRedis = redisUtil.getJwtToken(email);
        if (!refreshTokenInHeader.equals(refreshTokenStoredInRedis)) {
            throw new TickettyAppApplicationException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 새로운 access token 발급
        String newAccessToken = jwtTokenUtils.generateAccessToken(email);
        Date expiration = jwtTokenUtils.extractExpiration(newAccessToken);
        long accessTokenExpiration = expiration.getTime();

        // 기존 refresh token 삭제 후 새로운 refresh token 저장
        redisUtil.deleteJwtToken(email);
        String newRefreshToken = jwtTokenUtils.generateRefreshToken();
        redisUtil.saveJwtToken(email, newRefreshToken, refreshTokenExpiredTimeMs);

        return new UserLoginResponse(newAccessToken, newRefreshToken, accessTokenExpiration);
    }

}
