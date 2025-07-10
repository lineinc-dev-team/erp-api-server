package com.lineinc.erp.api.server.application.user;

import com.lineinc.erp.api.server.domain.company.entity.Company;
import com.lineinc.erp.api.server.domain.company.repository.CompanyRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${user.default-password}")
    private String defaultPassword;

    @Test
    @DisplayName("존재하는 로그인 ID로 사용자 조회 시 Users 반환")
    void getUserByLoginIdOrThrow_success() {
        // given
        Company company = Company.builder()
                .name("testCompany")
                .build();

        companyRepository.save(company);

        User user = User.builder()
                .company(company)
                .loginId("test123")
                .username("홍길동")
                .build();
        usersRepository.save(user);

        // when
        User found = userService.getUserByLoginIdOrThrow("test123");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getLoginId()).isEqualTo("test123");
    }

    @Test
    @DisplayName("존재하지 않는 로그인 ID로 조회 시 예외 발생")
    void getUserByLoginIdOrThrow_fail() {
        assertThatThrownBy(() -> userService.getUserByLoginIdOrThrow("not_exist"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("NOT_FOUND");
    }

    @Test
    @DisplayName("비밀번호 리셋 시 기본 비밀번호로 암호화하여 업데이트")
    void resetPassword_updatesPasswordHash() {
        // given
        Company company = Company.builder()
                .name("testCompany")
                .build();

        companyRepository.save(company);

        User user = User.builder()
                .company(company)
                .loginId("test123")
                .username("홍길동")
                .build();
        usersRepository.save(user);

        // when
        userService.resetPassword("test123");

        // then
        User updatedUser = usersRepository.findByLoginId("test123").orElseThrow();
        assertThat(passwordEncoder.matches(defaultPassword, updatedUser.getPassword())).isTrue();
        assertThat(updatedUser.getPasswordResetAt()).isNotNull();
    }

    @Test
    @DisplayName("마지막 로그인 시간 업데이트")
    void updateLastLoginAt_updatesTime() {
        // given
        Company company = Company.builder()
                .name("testCompany")
                .build();

        companyRepository.save(company);

        User user = User.builder()
                .company(company)
                .loginId("test123")
                .username("홍길동")
                .build();
        usersRepository.save(user);

        // when
        userService.updateLastLoginAt(user);

        // then
        User updatedUser = usersRepository.findByLoginId("test123").orElseThrow();
        assertThat(updatedUser.getLastLoginAt()).isNotNull();
        assertThat(updatedUser.getLastLoginAt()).isEqualTo(user.getLastLoginAt());
    }
}