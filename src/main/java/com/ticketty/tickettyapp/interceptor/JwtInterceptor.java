package com.ticketty.tickettyapp.interceptor;

import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.util.JwtTokenUtils;
import com.ticketty.tickettyapp.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // pre-flight 요청을 처리
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        // 헤더에 access token이 있는지 확인
        String accessTokenInHeader = jwtTokenUtils.extractAccessToken(request);
        if (accessTokenInHeader == null) {
            throw new TickettyAppApplicationException(ErrorCode.MISSING_ACCESS_TOKEN);
        }

        // 이미 로그아웃된 액세스 토큰인지 확인 (= blacklist 에 있는지)
        jwtTokenUtils.isAccessTokenLoggedOut(accessTokenInHeader);

        // 토큰의 유효성을 확인
        jwtTokenUtils.validateAccessToken(accessTokenInHeader);

        // 토큰이 유효하면 request에 이메일을 추가
        String userId = jwtTokenUtils.getSubject(accessTokenInHeader);
        request.setAttribute("userId", userId);

        return true;
    }
}
