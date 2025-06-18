package com.lineinc.erp.api.server.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/login", "/swagger-ui/**").permitAll()  // 로그인, 문서 접근 허용
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 비활성화
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // JWT 필터 등록
//
//        return http.build();
//    }
}