package com.lineinc.erp.api.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    /**
     * Spring Security 필터 체인을 설정하는 메서드
     * REST API 서버에 적합한 보안 구성을 적용
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CSRF 보호 비활성화
                // REST API는 기본적으로 세션을 사용하지 않고, 클라이언트가 상태를 관리하므로 CSRF 방어는 불필요
                .csrf(csrf -> csrf.disable())

                // ✅ 요청 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // Swagger 문서와 UI는 인증 없이 접근 허용 (API 문서 열람 목적)
                        .requestMatchers("/swagger-ui/**").permitAll()

                        // 그 외의 모든 요청은 인증 필요
                        // → 이후에 JWT 기반 인증 등으로 대체할 수 있음
                        .anyRequest().authenticated()
                )

                // ✅ HTTP Basic 인증 활성화 (브라우저 팝업으로 ID/PW 입력 받는 가장 기본적인 인증 방식)
                // - REST API 개발 초기에 간단히 테스트할 때 사용 가능
                // - 추후에는 JWT 또는 OAuth2 방식으로 대체하는 것이 일반적
                .httpBasic(Customizer.withDefaults());

        // 설정 완료 후 SecurityFilterChain 객체 생성하여 반환
        return http.build();
    }
}