package com.lineinc.erp.api.server.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * WebConfig
 * <p>
 * - CORS(Cross-Origin Resource Sharing) 전역 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final Environment environment;

    /**
     * CORS 매핑 전역 설정
     * <p>
     * - 세션 기반 인증을 위해 allowCredentials(true) 설정
     *
     * @param registry CORS 설정을 위한 객체
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        boolean isLocal = Arrays.asList(environment.getActiveProfiles()).contains("local");

        if (isLocal) {
            registry.addMapping("/**")   // 모든 엔드포인트에 대해 CORS 허용
                    .allowedOriginPatterns("*")     // 모든 Origin 허용
                    .allowedMethods("*")            // 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE 등)
                    .allowedHeaders("*")            // 모든 헤더 허용
                    .allowCredentials(true);        // 세션/쿠키 등의 자격 증명 허용
        }
    }
}