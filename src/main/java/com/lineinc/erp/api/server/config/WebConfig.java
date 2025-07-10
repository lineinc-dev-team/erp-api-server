package com.lineinc.erp.api.server.config;

import com.lineinc.erp.api.server.config.filter.RequestLoggingFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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

    private final Environment environment;

    /**
     * CORS 매핑 전역 설정
     * - 모든 엔드포인트 경로, HTTP 메서드, 헤더를 허용하며, 자격 증명(쿠키, 세션 등)도 함께 전달 가능
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://dev-erp.dooson.it") // 프론트엔드 오리진 명시
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // CORS 프리플라이트 요청 캐싱 시간
    }

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilter() {
        FilterRegistrationBean<RequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new RequestLoggingFilter()); // 필터 인스턴스 설정
        registrationBean.addUrlPatterns("/api/*");              // Swagger 등은 제외됨
        registrationBean.setOrder(1);                           // 필터 체인 내 우선순위 설정 (낮을수록 먼저 실행)

        return registrationBean;
    }
}