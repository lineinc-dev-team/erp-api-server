package com.lineinc.erp.api.server.application.auth;

import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest  // 실제 스프링 컨텍스트를 띄워서 통합 테스트 수행
@Transactional  // 테스트 종료 후 자동 롤백 처리, DB 상태 유지
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository usersRepository;


    @Test
    @DisplayName("정상적인 로그인 ID를 주면 UserDetails 반환")
    void loadUserByUsername_success() {
        User user = User.builder()
                .loginId("test123")
                .username("test")
                .build();

        usersRepository.save(user);  // 사용자 저장

        // when: 로그인 ID로 UserDetails 조회
        UserDetails result = authService.loadUserByUsername("test123");

        // then: UserDetails가 null이 아니고, 사용자명 일치 확인
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test");
    }

    @Test
    @DisplayName("존재하지 않는 로그인 ID를 주면 예외 발생")
    void loadUserByUsername_fail() {
        // when-then: 존재하지 않는 ID로 조회 시 BadCredentialsException 발생하는지 검증
        assertThatThrownBy(() -> authService.loadUserByUsername("not_exist"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }
}