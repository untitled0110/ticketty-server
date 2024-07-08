package com.ticketty.tickettyapp.service;

import com.ticketty.tickettyapp.controller.response.Response;
import com.ticketty.tickettyapp.controller.response.UserLoginResponse;
import com.ticketty.tickettyapp.exception.ErrorCode;
import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.model.User;
import com.ticketty.tickettyapp.model.entity.UserEntity;
import com.ticketty.tickettyapp.repository.UserEntityRepository;
import com.ticketty.tickettyapp.util.JwtTokenUtils;
import com.ticketty.tickettyapp.util.NicknameGenerator;
import com.ticketty.tickettyapp.util.PasswordEncoder;
import com.ticketty.tickettyapp.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {

    @Value("${jwt.token.expired-time-ms.refresh}")
    private Long refreshTokenExpiredTimeMs;

    @Value("${niceapi.client_id}")
    private String clientId;

    @Value("${niceapi.client_secret}")
    private String clientSecret;

    @Value("${niceapi.product_id}")
    private String productId;

    @Value("${niceapi.get_token_url}")
    private String getTokenUrl;

    @Value("${niceapi.account_holder_url}")
    private String accountHolderUrl;

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final JwtTokenUtils jwtTokenUtils;
    private final RestTemplate restTemplate;

    @Transactional
    public User signUpUser(String email, String password) {
        // 랜덤 닉네임 생성
        String randomNickname = NicknameGenerator.generateNickname();
        System.out.println("randomNickname!!!!!!!"+ randomNickname);

        // 회원가입 진행 = user를 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(email, randomNickname, passwordEncoder.encrypt(email, password)));
        return User.fromEntity(userEntity);
    }

    public UserLoginResponse login(String email, String password) {
        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByEmail(email).orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", email)));
        Integer userId = userEntity.getId();

        // 비밀번호 체크
        if (!passwordEncoder.matches(email, password, userEntity.getPassword())) {
            throw new TickettyAppApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String accessToken = jwtTokenUtils.generateAccessToken(email, userId);
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
        UserEntity userEntity = userEntityRepository.findByEmail(emailFromAccessToken).orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", emailFromAccessToken)));
        Integer userId = userEntity.getId();

        // refresh token이 Redis에 있는지 확인 후 헤더의 refresh token과 비교
        compareRefreshTokens(refreshTokenInHeader, emailFromAccessToken);


        // 새로운 access token 발급
        String newAccessToken = jwtTokenUtils.generateAccessToken(emailFromAccessToken, userId);
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
        userEntityRepository.findByEmail(emailFromAccessToken).orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", emailFromAccessToken)));

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
                .orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", email)));
        userEntity.setPassword(passwordEncoder.encrypt(email, password));
        userEntityRepository.save(userEntity);
    }

    public User getUserInfo(Integer userId) {
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND));

        return User.fromEntity(userEntity);
    }

    @Transactional
    public void changeNickname(Integer userId, String newNickname) {
//        UserEntity userEntity = userEntityRepository.findById(userId)
//                .orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND));

        Optional<UserEntity> existingUser = userEntityRepository.findByNickname(newNickname);
        if (existingUser.isPresent()) {
            throw new TickettyAppApplicationException(ErrorCode.DUPLICATED_NICKNAME, "닉네임이 이미 사용 중입니다.");
        }

        userEntityRepository.updateNicknameById(userId, newNickname);
    }

    @Transactional
    public void changePhone(Integer userId, String phoneNumber) {
//        UserEntity userEntity = userEntityRepository.findById(userId)
//                .orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND));

        Optional<UserEntity> existingUser = userEntityRepository.findByPhone(phoneNumber);
        if (existingUser.isPresent()) {
            throw new TickettyAppApplicationException(ErrorCode.DUPLICATED_PHONE, "휴대폰 번호가 이미 사용 중입니다.");
        }

        userEntityRepository.updatePhoneById(userId, phoneNumber);
    }

    @Transactional
    public void changeAccount(Integer userId, String accountNumber, String bankName, String accountHolder) {
        userEntityRepository.updateAccountInfoById(userId, accountNumber, bankName, accountHolder);
    }

    @Transactional
    public void changeEmoji(Integer userId, String emoji) {
        userEntityRepository.updateEmojiById(userId, emoji);
    }

    @Transactional
    public Response<Void> verifyAccount(String acctGb, String bnkCd, String acctNo, String name, Integer userId) {

        UserEntity existingUser = userEntityRepository.findById(userId)
                .orElseThrow(() -> new TickettyAppApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userId)));

        if (bnkCd.equals(existingUser.getBankName()) && acctNo.equals(existingUser.getAccountNumber())) {
            throw new TickettyAppApplicationException(ErrorCode.ALREADY_REGISTERED_ACCOUNT);
        }

        boolean accountExists = userEntityRepository.existsByBankNameAndAccountNumber(bnkCd, acctNo);
        if (accountExists) {
            throw new TickettyAppApplicationException(ErrorCode.DUPLICATED_ACCOUNT);
        }

        String accessToken = getAccessToken();

        String timestamp = String.valueOf(new Date().getTime() / 1000);
        String authorization = "bearer " + Base64.getEncoder().encodeToString((accessToken + ":" + timestamp + ":" + clientId).getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorization);
        headers.set("client_id", clientId);
        headers.set("productID", productId);

        Map<String, Object> body = new HashMap<>();
        Map<String, String> dataHeader = new HashMap<>();
        dataHeader.put("CNTY_CD", "ko");
        body.put("dataHeader", dataHeader);

        Map<String, String> dataBody = new HashMap<>();
        dataBody.put("acct_gb", acctGb);
        dataBody.put("bnk_cd", bnkCd);
        dataBody.put("acct_no", acctNo);
        dataBody.put("name", name);
        body.put("dataBody", dataBody);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                accountHolderUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        System.out.println("Response from account holder API: " + response.getBody());

        Map<String, Object> responseBody = response.getBody();
        Map<String, Object> responseDataHeader = (Map<String, Object>) responseBody.get("dataHeader");

        String gwResultCode = (String) responseDataHeader.get("GW_RSLT_CD");
        if (!"1200".equals(gwResultCode)) {
            throw new TickettyAppApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "External API error with GW_RSLT_CD: " + gwResultCode);
        }

        Map<String, Object> responseDataBody = (Map<String, Object>) responseBody.get("dataBody");
        String rspCd = (String) responseDataBody.get("rsp_cd");
        String resultCd = (String) responseDataBody.get("result_cd");


        if ("P000".equals(rspCd)) {
            if (isInputValueError(resultCd)) {
                throw new TickettyAppApplicationException(ErrorCode.INPUT_VALUE_ERROR, "Input value error with RESULT_CD: " + resultCd);
            }

            if (isServerError(resultCd)) {
                throw new TickettyAppApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Server error with RESULT_CD: " + resultCd);
            }

            if ("D999".equals(resultCd)) {
                throw new TickettyAppApplicationException(ErrorCode.NOT_SERVICE_TIME, "Service not available with RESULT_CD: " + resultCd);
            }
        }

        if (isInputValueError(rspCd)) {
            throw new TickettyAppApplicationException(ErrorCode.INPUT_VALUE_ERROR, "Input value error with RSP_CD: " + rspCd);
        }

        if (isServerError(rspCd)) {
            throw new TickettyAppApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Server error with RSP_CD: " + rspCd);
        }

        // 유저 테이블 계좌 정보 업데이트
//        updateUserAccountInfo(userId, acctNo, bnkCd, name);

        return new Response<>("SUCCESS", null);
    }

    private boolean isInputValueError(String code) {
        return Arrays.asList("P001", "P005", "S700", "S315", "L399", "DB01", "D900", "D103", "D105", "B004", "B102", "B103", "B104", "S606").contains(code);
    }

    private boolean isServerError(String code) {
        return Arrays.asList("P013", "S691", "E998", "E999", "TIME", "DSYS", "OVER", "D888", "B101", "B199").contains(code) || code.startsWith("E");
    }

//    private void updateUserAccountInfo(Integer userId, String accountNumber, String bankName, String accountHolder) {
//        Timestamp accountRegisteredAt = Timestamp.from(Instant.now());
//        userEntityRepository.updateAccountInfo(userId, accountNumber, bankName, accountHolder, accountRegisteredAt);
//    }

    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String auth = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        headers.set("Authorization", auth);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", "default");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                getTokenUrl,
                entity,
                Map.class
        );

        System.out.println("Response from token API: " + response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get("dataBody");
        return (String) responseBody.get("access_token");
    }


}


