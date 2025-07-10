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

        // CORS 설정 등록
        registry.addMapping("/**")        // 모든 API 엔드포인트 허용
                .allowedOriginPatterns("*")
                .allowedMethods("*")                 // GET, POST, PUT, DELETE 등 모든 HTTP 메서드 허용
                .allowedHeaders("*")                 // 모든 요청 헤더 허용
                .allowCredentials(true);             // 쿠키/세션 등의 인증 정보 포함 허용
    }

    /**
     * 크로스 도메인 쿠키 설정
     * - 로컬 개발 환경에서 다른 포트 간 쿠키 전송을 위한 설정
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        serializer.setCookiePath("/");
        serializer.setSameSite("None");              // 크로스 도메인 쿠키 허용
        serializer.setUseSecureCookie(false);        // 개발환경에서는 false (HTTPS 아닌 환경)
        serializer.setUseHttpOnlyCookie(true);       // XSS 방지
        return serializer;
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