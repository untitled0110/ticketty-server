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

import static com.ticketty.tickettyapp.exception.ErrorCode.USER_NOT_FOUND;

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
        // 회원가입 진행 = user를 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(email, passwordEncoder.encrypt(email, password)));
        return User.fromEntity(userEntity);
    }

    public UserLoginResponse login(String email, String password) {
        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByEmail(email).orElseThrow(() -> new TickettyAppApplicationException(USER_NOT_FOUND, String.format("%s not founded", email)));

        // 비밀번호 체크
        if (!passwordEncoder.matches(email, password, userEntity.getPassword())) {
            throw new TickettyAppApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String accessToken = jwtTokenUtils.generateAccessToken(email);
        String refreshToken = jwtTokenUtils.generateRefreshToken();

        Date expiration = jwtTokenUtils.extractExpiration(accessToken);

        long accessTokenExpiration = expiration.getTime();

        // Redis에 refresh token 저장
        redisUtil.saveJwtToken(email, refreshToken, refreshTokenExpiredTimeMs);

        return new UserLoginResponse(accessToken, refreshToken, accessTokenExpiration);
    }

    public UserLoginResponse reissueAccessToken(HttpServletRequest httpServletRequest) {
        // 헤더에서 토큰 추출
        String accessTokenInHeader = jwtTokenUtils.extractAccessToken(httpServletRequest);
        String refreshTokenInHeader = jwtTokenUtils.extractRefreshToken(httpServletRequest);

        // 헤더에 토큰이 있는지 확인
        hasTokenInHeader(accessTokenInHeader, refreshTokenInHeader);

        // 이미 로그아웃된 access token 인지 확인 (= blacklist 에 있는지)
        jwtTokenUtils.isAccessTokenLoggedOut(accessTokenInHeader);

        // refresh token 토큰 유효성 검증 (파싱 되는지, 유효 시간 지났는지 확인)
        jwtTokenUtils.validateRefreshToken(refreshTokenInHeader);

        // access token 으로 이메일 추출 (+ access token 이 파싱 되는지 확인)
        String emailFromAccessToken = jwtTokenUtils.extractEmailFromExpiredAccessToken(accessTokenInHeader);
        // email로 회원가입 여부 체크
        userEntityRepository.findByEmail(emailFromAccessToken).orElseThrow(() -> new TickettyAppApplicationException(USER_NOT_FOUND, String.format("%s not founded", emailFromAccessToken)));

        // refresh token이 Redis에 있는지 확인 후 헤더의 refresh token과 비교
        compareRefreshTokens(refreshTokenInHeader, emailFromAccessToken);


        // 새로운 access token 발급
        String newAccessToken = jwtTokenUtils.generateAccessToken(emailFromAccessToken);
        Date expiration = jwtTokenUtils.extractExpiration(newAccessToken);
        long accessTokenExpiration = expiration.getTime();

        // 기존 refresh token 삭제 후 새로운 refresh token 저장
        redisUtil.deleteRefreshTokenInRedis(emailFromAccessToken);
        String newRefreshToken = jwtTokenUtils.generateRefreshToken();
        redisUtil.saveJwtToken(emailFromAccessToken, newRefreshToken, refreshTokenExpiredTimeMs);

        return new UserLoginResponse(newAccessToken, newRefreshToken, accessTokenExpiration);
    }

    public void logout(HttpServletRequest httpServletRequest) {

        // 헤더에서 토큰 추출
        String accessTokenInHeader = jwtTokenUtils.extractAccessToken(httpServletRequest);
        String refreshTokenInHeader = jwtTokenUtils.extractRefreshToken(httpServletRequest);

        // 헤더에 토큰이 있는지 확인
        hasTokenInHeader(accessTokenInHeader, refreshTokenInHeader);

        // 이미 로그아웃된 access token 인지 확인 (= blacklist 에 있는지)
        jwtTokenUtils.isAccessTokenLoggedOut(accessTokenInHeader);

        // access token 으로 이메일 추출 (+ access token 이 파싱 되는지 확인)
        String emailFromAccessToken = jwtTokenUtils.extractEmailFromExpiredAccessToken(accessTokenInHeader);
        // email로 회원가입 여부 체크
        userEntityRepository.findByEmail(emailFromAccessToken).orElseThrow(() -> new TickettyAppApplicationException(USER_NOT_FOUND, String.format("%s not founded", emailFromAccessToken)));

        // access token 을 Redis에 블랙리스트로 저장한다.
        redisUtil.saveBlacklistedAccessToken(accessTokenInHeader);

        // refresh token 토큰 유효성 검증 (파싱 되는지 확인)
        jwtTokenUtils.validateRefreshTokenForLogOut(refreshTokenInHeader);

        // refresh token이 Redis에 있는지 확인 후 헤더의 refresh token과 비교
        compareRefreshTokens(refreshTokenInHeader, emailFromAccessToken);

        // Redis에 저장되어 있는 refresh token 삭제
        String email = jwtTokenUtils.extractEmailFromExpiredAccessToken(accessTokenInHeader);
        redisUtil.deleteRefreshTokenInRedis(email);
    }

    public void hasTokenInHeader(String accessTokenInHeader, String refreshTokenInHeader) {

        if (accessTokenInHeader == null) {
            throw new TickettyAppApplicationException(ErrorCode.MISSING_ACCESS_TOKEN);
        }
        if (refreshTokenInHeader == null) {
            throw new TickettyAppApplicationException(ErrorCode.MISSING_REFRESH_TOKEN);
        }
    }

    public void compareRefreshTokens(String refreshTokenInHeader, String email) {

        String refreshTokenStoredInRedis = redisUtil.getJwtToken(email);
        if (!refreshTokenInHeader.equals(refreshTokenStoredInRedis)) {
            throw new TickettyAppApplicationException(ErrorCode.INVALID_REFRESH_TOKEN, ("Two refresh tokens are different"));
        }
    }

    @Transactional
    public void changePassword(String email, String password) {
        UserEntity userEntity = userEntityRepository.findByEmail(email)
                .orElseThrow(() -> new TickettyAppApplicationException(USER_NOT_FOUND, String.format("%s not founded", email)));
        userEntity.setPassword(passwordEncoder.encrypt(email, password));
        userEntityRepository.save(userEntity);
    }

}


