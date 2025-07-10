package com.lineinc.erp.api.server.config.filter;

import com.lineinc.erp.api.server.common.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.time.Duration;
import java.time.Instant;

/**
 * HTTP 요청/응답에 대한 로깅을 처리하는 필터
 * 모든 API 요청에 대해 IP, UserAgent, 응답시간, 상태코드 등을 로깅합니다.
 */
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    /**
     * HTTP 요청을 가로채서 로깅 정보를 수집하고 기록합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param chain    필터 체인
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // ServletRequest를 HttpServletRequest로 캐스팅
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 요청 정보 수집
        String clientIp = HttpUtils.getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();

        // 요청 시작 시간 기록 (응답 시간 측정용)
        Instant startTime = Instant.now();

        try {
            // 다음 필터 또는 서블릿으로 요청 전달
            chain.doFilter(request, response);
        } finally {
            // 예외 발생 여부와 관계없이 항상 로그 기록
            // 응답 시간 계산
            long duration = Duration.between(startTime, Instant.now()).toMillis();
            int status = httpResponse.getStatus();

            // 구조화된 로그 출력
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("method", method);
            logMap.put("uri", uri + (queryString != null ? "?" + queryString : ""));
            logMap.put("ip", clientIp);
            logMap.put("status", status);
            logMap.put("duration", duration);
            logMap.put("userAgent", userAgent != null ? userAgent : "unknown");

            logger.info("{}", logMap);
        }
    }

}