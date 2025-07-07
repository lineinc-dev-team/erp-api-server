package com.lineinc.erp.api.server.application.users;

import com.lineinc.erp.api.server.domain.company.entity.Company;
import com.lineinc.erp.api.server.domain.company.repository.CompanyRepository;
import com.lineinc.erp.api.server.domain.users.entity.Users;
import com.lineinc.erp.api.server.domain.users.repository.UsersRepository;
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
class UsersServiceTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersRepository usersRepository;

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

        Users user = Users.builder()
                .company(company)
                .loginId("test123")
                .username("홍길동")
                .build();
        usersRepository.save(user);

        // when
        Users found = usersService.getUserByLoginIdOrThrow("test123");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getLoginId()).isEqualTo("test123");
    }

    @Test
    @DisplayName("존재하지 않는 로그인 ID로 조회 시 예외 발생")
    void getUserByLoginIdOrThrow_fail() {
        assertThatThrownBy(() -> usersService.getUserByLoginIdOrThrow("not_exist"))
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

        Users user = Users.builder()
                .company(company)
                .loginId("test123")
                .username("홍길동")
                .build();
        usersRepository.save(user);

        // when
        usersService.resetPassword("test123");

        // then
        Users updatedUser = usersRepository.findByLoginId("test123").orElseThrow();
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

        Users user = Users.builder()
                .company(company)
                .loginId("test123")
                .username("홍길동")
                .build();
        usersRepository.save(user);

        // when
        usersService.updateLastLoginAt(user);

        // then
        Users updatedUser = usersRepository.findByLoginId("test123").orElseThrow();
        assertThat(updatedUser.getLastLoginAt()).isNotNull();
        assertThat(updatedUser.getLastLoginAt()).isEqualTo(user.getLastLoginAt());
    }
}