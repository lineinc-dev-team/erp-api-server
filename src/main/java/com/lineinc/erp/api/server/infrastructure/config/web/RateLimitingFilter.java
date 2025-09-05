package com.lineinc.erp.api.server.infrastructure.config.web;

import com.lineinc.erp.api.server.domain.common.service.RateLimitService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Rate Limiting 필터
 * - 로그인된 사용자: 사용자별 제한 (1분당 200개)
 * - 비로그인 사용자: IP별 제한 (1분당 50개)
 * - 과도한 요청 시 429 Too Many Requests 응답
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    // Rate Limiting 설정
    private static final int AUTHENTICATED_REQUESTS_PER_MINUTE = 200; // 로그인 사용자: 1분당 최대 요청 수
    private static final int ANONYMOUS_REQUESTS_PER_MINUTE = 50; // 비로그인 사용자: 1분당 최대 요청 수
    private static final int TIME_WINDOW_SECONDS = 60; // 시간 윈도우 (초)

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 제외할 경로들 (Swagger, Actuator 등)
        String requestURI = request.getRequestURI();
        if (shouldSkipRateLimit(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 식별자와 제한값 결정
        String identifier;
        int limitPerMinute;

        // 로그인된 사용자인지 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // 로그인된 사용자: 사용자명 기준
            identifier = "user:" + auth.getName();
            limitPerMinute = AUTHENTICATED_REQUESTS_PER_MINUTE;
        } else {
            // 비로그인 사용자: IP 기준
            identifier = "ip:" + getClientIpAddress(request);
            limitPerMinute = ANONYMOUS_REQUESTS_PER_MINUTE;
        }

        // Rate Limiting 버킷 조회
        Bucket bucket = rateLimitService.resolveBucket(identifier, limitPerMinute, TIME_WINDOW_SECONDS);

        // 토큰 소비 시도
        if (bucket.tryConsume(1)) {
            // 요청 허용
            filterChain.doFilter(request, response);
        } else {
            // Rate Limit 초과
            log.warn("Rate limit exceeded for identifier: {} on URI: {}", identifier, requestURI);
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter()
                    .write("{\"error\":\"Too Many Requests\",\"message\":\"요청이 너무 많습니다. 잠시 후 다시 시도해주세요.\"}");
        }
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Rate Limiting을 건너뛸 경로들
     */
    private boolean shouldSkipRateLimit(String requestURI) {
        return requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/actuator") ||
                requestURI.equals("/favicon.ico") ||
                requestURI.equals("/");
    }
}
