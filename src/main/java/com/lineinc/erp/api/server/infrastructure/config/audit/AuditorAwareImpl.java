package com.lineinc.erp.api.server.infrastructure.config.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;

import lombok.NonNull;

/**
 * Spring Data JPA의 Auditing 기능에서 사용하는 AuditorAware 구현체입니다.
 * 현재 로그인한 사용자의 이름(아이디) 형태를 반환하여 생성자/수정자 필드에 자동으로 채워주기 위해 사용됩니다.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    private final UserRepository userRepository;

    public AuditorAwareImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        final Object principal = authentication.getPrincipal();

        // anonymousUser 문자열 체크 추가
        if (principal instanceof String && "anonymousUser".equals(principal)) {
            return Optional.empty();
        }

        if (principal instanceof CustomUserDetails) {
            final CustomUserDetails userDetails = (CustomUserDetails) principal;
            try {
                // 사용자 ID로 실제 사용자 정보 조회
                final User user = userRepository.findById(userDetails.getUserId()).orElse(null);
                if (user != null) {
                    final String username = user.getUsername() != null ? user.getUsername() : "이름없음";
                    final String loginId = user.getLoginId();
                    return Optional.of(username + "(" + loginId + ")");
                }
            } catch (final Exception e) {
                // 사용자 조회 실패 시 아이디만 반환
                return Optional.of(userDetails.getUsername());
            }
        } else if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        } else {
            return Optional.empty();
        }

        return Optional.empty();
    }
}