package com.ticketty.tickettyapp;

import com.ticketty.tickettyapp.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            System.out.println("token!!!!!!!!!!! "+ token);
            if (jwtTokenUtils.validateToken(token)) {

                String email = jwtTokenUtils.getSubject(token);
                request.setAttribute("email", email);

                return true;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
