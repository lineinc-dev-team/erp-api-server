package com.lineinc.erp.api.server.infrastructure.config.web;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 웹 필터 설정
 */
@Configuration
@RequiredArgsConstructor
public class WebFilterConfig {

    private final RateLimitingFilter rateLimitingFilter;

    /**
     * Rate Limiting 필터 등록
     * - Security 필터보다 앞서 실행되도록 설정
     */
    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterRegistration() {
        FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(rateLimitingFilter);
        registration.addUrlPatterns("/api/*"); // API 경로에만 적용
        registration.setOrder(1); // 가장 먼저 실행
        registration.setName("rateLimitingFilter");
        return registration;
    }
}
