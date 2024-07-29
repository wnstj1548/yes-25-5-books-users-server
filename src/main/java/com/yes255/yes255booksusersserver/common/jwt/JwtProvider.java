package com.yes255.yes255booksusersserver.common.jwt;

import com.yes255.yes255booksusersserver.common.exception.JwtException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.presentation.dto.response.JwtAuthResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private static final String ISSUER = "auth-server";
    private final SecretKey secretKey;

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public boolean isValidToken(String token) {
        try {
            Jws<Claims> claimJets = Jwts.parser()
                                    .verifyWith(secretKey)
                                    .build()
                                    .parseSignedClaims(token);

            Claims claims = claimJets.getPayload();

            if (claims.getExpiration().before(new Date())) {
                throw new JwtException(
                        ErrorStatus.toErrorStatus("토큰의 유효시간이 지났습니다.", 401, LocalDateTime.now())
                );
            }

            return true;
        } catch (SignatureException e) {
            throw new JwtException(
                    ErrorStatus.toErrorStatus("시크릿키 변경이 감지되었습니다.", 401, LocalDateTime.now())
            );
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public JwtAuthResponse getJwtAuthFromToken(String token) {
        try {
            Claims claims = parseToken(token);

            Long userId = claims.get("userId", Long.class);
            String userRole = claims.get("userRole", String.class);
            String loginStatusName = claims.get("loginStatus", String.class);

            return JwtAuthResponse.builder()
                    .customerId(userId)
                    .role(userRole)
                    .loginStateName(loginStatusName).build();
        } catch (ExpiredJwtException e) {
            throw new JwtException(ErrorStatus.toErrorStatus("JWT token is expired", 401, LocalDateTime.now()));
        } catch (Exception e) {
            throw new JwtException(ErrorStatus.toErrorStatus("Invalid JWT token", 401, LocalDateTime.now()));
        }
    }
}