package com.lineinc.erp.api.server.infrastructure.config.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    /**
     * CORS 매핑 전역 설정
     * - 모든 엔드포인트 경로, HTTP 메서드, 헤더를 허용하며, 자격 증명(쿠키, 세션 등)도 함께 전달 가능
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 모든 origin 허용
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilter() {
        FilterRegistrationBean<RequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new RequestLoggingFilter()); // 필터 인스턴스 설정
        registrationBean.addUrlPatterns("/api/*"); // Swagger 등은 제외됨
        registrationBean.setOrder(1); // 필터 체인 내 우선순위 설정 (낮을수록 먼저 실행)

        return registrationBean;
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setUseHttpOnlyCookie(true); // HttpOnly 설정
        serializer.setSameSite("None"); // 크로스 도메인 쿠키 허용
        serializer.setUseSecureCookie(true); // HTTPS 환경에서만 쿠키 전송

        return serializer;
    }
}