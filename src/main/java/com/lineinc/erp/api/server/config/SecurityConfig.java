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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

                // 엔드포인트별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로 설정
                        .requestMatchers(
                                "api/v1/auth/login", "api/v1/auth/logout",      // 인증 API
                                "/swagger-ui/**", "/v3/api-docs/**",          // Swagger 문서
                                "/swagger-ui.html"                            // Swagger UI HTML
                        ).permitAll()
                        // 그 외 요청은 인증 필요
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

    /**
     * 이 메서드는 인증 시 사용자 입력 비밀번호(평문)와 DB에 저장된 비밀번호(해시)를 비교할 때 사용합니다.
     * - BCrypt는 해시 + 솔트가 포함된 강력한 암호화 알고리즘으로, 비밀번호 보안을 위해 가장 많이 사용됩니다.
     * <p>
     * 사용 예시:
     * passwordEncoder.encode("plainPassword"); // 비밀번호 해시화
     * passwordEncoder.matches("plain", "hashed"); // 평문 vs 해시 비교
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}