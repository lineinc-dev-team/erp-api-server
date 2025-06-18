package com.lineinc.erp.api.server.config;

import com.lineinc.erp.api.server.application.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 보안 설정 클래스
 */
@Configuration  // 스프링 설정 클래스 선언
@EnableWebSecurity  // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthService authService;

    /**
     * 보안 필터 체인 설정
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // REST API에서는 CSRF 보호 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // REST API에서는 HTML 폼 로그인을 사용하지 않음
                .formLogin(AbstractHttpConfigurer::disable)

                // 요청 URL 별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인, 로그아웃 엔드포인트는 인증 없이 접근 가능
                        .requestMatchers("/auth/login", "/auth/logout").permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 세션 기반 인증 설정: 인증 시 세션 생성
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 사용자 인증 처리를 위한 커스텀 UserDetailsService 등록
                .userDetailsService(authService)
                .build();
    }

    /**
     * 이 메서드는 HttpSecurity에서 공유 객체로 등록된 AuthenticationManagerBuilder를 가져와 빌드합니다.
     *
     * @param http HttpSecurity 설정 객체
     * @return AuthenticationManager 인스턴스
     * @throws Exception 빌드 중 발생할 수 있는 예외
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }
}