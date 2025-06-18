package com.lineinc.erp.api.server.auth;

import com.lineinc.erp.api.server.domain.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private java.time.Duration accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private java.time.Duration refreshTokenValidity;

    private SecretKey signingKey;

    // 빈 초기화 시 SecretKey 생성
    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Access Token 생성
     *
     * @param user 토큰에 식별 정보를 담을 사용자
     * @return 서명된 JWT Access Token 문자열
     */

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity.toMillis()))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 생성
     *
     * @param user 사용자 정보
     * @return 서명된 JWT Refresh Token 문자열
     */
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity.toMillis()))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     *
     * @param token JWT 문자열
     * @return 사용자 ID (Long 타입)
     */
    public Long extractUserId(String token) {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    /**
     * JWT 토큰 유효성 검증
     *
     * @param token JWT 문자열
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}