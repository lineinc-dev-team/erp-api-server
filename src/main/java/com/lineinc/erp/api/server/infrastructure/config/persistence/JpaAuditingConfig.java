package com.lineinc.erp.api.server.infrastructure.config.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.infrastructure.config.audit.AuditorAwareImpl;

/**
 * JPA Auditing 설정 클래스입니다.
 * - @EnableJpaAuditing을 통해 JPA Auditing 기능 활성화
 */
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Configuration
public class JpaAuditingConfig {

    /**
     * AuditorAware 빈 등록
     * - 현재 인증된 사용자명을 제공하는 AuditorAwareImpl을 빈으로 등록
     *
     * @return AuditorAware<String> 구현체
     */
    @Bean
    public AuditorAware<String> auditorAware(final UserRepository userRepository) {
        return new AuditorAwareImpl(userRepository);
    }
}