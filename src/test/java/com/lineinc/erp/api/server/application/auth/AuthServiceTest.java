package com.lineinc.erp.api.server.application.auth;

import com.lineinc.erp.api.server.domain.users.Users;
import com.lineinc.erp.api.server.domain.users.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("정상적인 로그인 ID를 주면 UserDetails 반환")
    void loadUserByUsername_success() {
        // given
        Users user = new Users(
                null,
                "test",
                "홍길동",
                "password",
                "010-1234-5678",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null
        );
        // Mock 객체 동작 정의
        given(usersRepository.findByLoginId("test")).willReturn(Optional.of(user));

        // when
        UserDetails result = authService.loadUserByUsername("test");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("존재하지 않는 로그인 ID를 주면 예외 발생")
    void loadUserByUsername_fail() {
        // given : usersRepository에서 해당 loginId가 없으면 Optional.empty() 반환
        given(usersRepository.findByLoginId("not_exist")).willReturn(Optional.empty());

        // when & then : 예외가 발생하는지 검증
        assertThatThrownBy(() -> authService.loadUserByUsername("not_exist"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("존재하지 않는 사용자입니다");
    }
}