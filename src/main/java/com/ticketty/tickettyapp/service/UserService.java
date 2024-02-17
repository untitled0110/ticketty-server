package com.ticketty.tickettyapp.service;

import com.ticketty.tickettyapp.controller.response.UserLoginResponse;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.model.User;
import com.ticketty.tickettyapp.model.entity.UserEntity;
import com.ticketty.tickettyapp.repository.UserEntityRepository;
import com.ticketty.tickettyapp.util.JwtTokenUtils;
import com.ticketty.tickettyapp.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

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
        String accessToken = JwtTokenUtils.generateAccessToken(email, secretKey, expiredTimeMs);
        String refreshToken = JwtTokenUtils.generateRefreshToken(secretKey, expiredTimeMs);

        // 액세스 토큰의 만료 시간 계산 (현재 시간 + 만료 시간)
        long accessTokenExpiration = System.currentTimeMillis() + expiredTimeMs;

        return new UserLoginResponse(accessToken, refreshToken, accessTokenExpiration);
    }

}
