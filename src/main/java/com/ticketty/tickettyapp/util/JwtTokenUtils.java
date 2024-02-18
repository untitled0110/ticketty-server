package com.ticketty.tickettyapp.util;

import com.ticketty.tickettyapp.exception.TickettyAppApplicationException;
import com.ticketty.tickettyapp.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtils {

    @Value("${jwt.secret-key}")
    private String secretKey = "ji.sns-application-2024.secret_key";

    @Value("${jwt.token.expired-time-ms.access}")
    private Long accessTokenExpiredTimeMs;

    @Value("${jwt.token.expired-time-ms.refresh}")
    private Long refreshTokenExpiredTimeMs;

    public String generateAccessToken(String email) {
        Claims claims = Jwts.claims();
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(email))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiredTimeMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken() {

        return Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiredTimeMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token) {
        try {
            // 토큰 파싱
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 유효 시간이 지난 경우
            System.err.println("Token has expired: " + e.getMessage());
            throw new TickettyAppApplicationException(ErrorCode.EXPIRED_ACCESS_TOKEN, "Expired Access token");
        } catch (JwtException e) {
            // 토큰 파싱에 실패한 경우
            System.err.println("Error parsing token: " + e.getMessage());
            throw new TickettyAppApplicationException(ErrorCode.INVALID_ACCESS_TOKEN, "Error parsing Access token");
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            // 토큰 파싱
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 유효 시간이 지난 경우
            System.err.println("Token has expired: " + e.getMessage());
            throw new TickettyAppApplicationException(ErrorCode.EXPIRED_REFRESH_TOKEN, "Expired Refresh token");
        } catch (JwtException e) {
            // 토큰 파싱에 실패한 경우
            System.err.println("Error parsing token: " + e.getMessage());
            throw new TickettyAppApplicationException(ErrorCode.INVALID_REFRESH_TOKEN, "Error parsing Refresh token");
        }
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
    }

    public Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

//    public String getSubject(String token) {
//        return parseToken(token).getBody().getSubject();
//    }

    public String getSubject(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public String extractAccessToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public String extractRefreshToken(HttpServletRequest request) {
        String token = request.getHeader("Refresh");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public String extractEmailFromExpiredAccessToken(String accessToken) {
        try {
            Claims claims = parseToken(accessToken).getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰인 경우에도 클레임을 추출하여 반환
            return e.getClaims().getSubject();
        } catch (JwtException e) {
            // 토큰 파싱에 실패한 경우 예외 처리
            throw new TickettyAppApplicationException(ErrorCode.INVALID_ACCESS_TOKEN, "Invalid Access token");
        }
    }

}
