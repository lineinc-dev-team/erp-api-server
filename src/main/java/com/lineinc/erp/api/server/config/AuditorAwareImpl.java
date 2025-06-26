package com.lineinc.erp.api.server.config;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Spring Data JPA의 Auditing 기능에서 사용하는 AuditorAware 구현체입니다.
 * 현재 로그인한 사용자의 아이디를 반환하여 생성자/수정자 필드에 자동으로 채워주기 위해 사용됩니다.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * 현재 인증된 사용자의 아이디를 Optional<String> 형태로 반환합니다.
     * - 인증 정보가 없거나 인증되지 않은 경우 빈 Optional 반환
     * - UserDetails 타입일 경우 username 반환
     * - principal이 String 타입일 경우 그대로 반환
     * - 그 외에는 빈 Optional 반환
     *
     * @return Optional.of(사용자아이디) 또는 Optional.empty()
     */
    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        // 현재 SecurityContext에서 인증 객체를 얻음
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나 인증되지 않은 경우 빈값 반환
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        // 인증된 사용자 정보(principal) 조회
        Object principal = authentication.getPrincipal();

        // principal이 UserDetails 타입이면 username을 Optional에 담아 반환
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return Optional.of(((org.springframework.security.core.userdetails.UserDetails) principal).getUsername());

            // principal이 String 타입일 경우 그대로 Optional에 담아 반환
        } else if (principal instanceof String) {
            return Optional.of((String) principal);

            // 그 외 타입은 빈 Optional 반환
        } else {
            return Optional.empty();
        }
    }
}