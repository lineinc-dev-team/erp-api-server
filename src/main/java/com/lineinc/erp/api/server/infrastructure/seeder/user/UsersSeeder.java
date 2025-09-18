package com.lineinc.erp.api.server.infrastructure.seeder.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsersSeeder {

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public void seed() {
        final Optional<User> existingAdmin = usersRepository.findByLoginIdAndDeletedFalse(AppConstants.ADMIN_LOGIN_ID);
        if (existingAdmin.isPresent())
            return;

        final User admin = User.builder()
                .loginId(AppConstants.ADMIN_LOGIN_ID)
                .username(AppConstants.ADMIN_USERNAME)
                .email(AppConstants.ADMIN_EMAIL)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .requirePasswordReset(false)
                .isHeadOffice(true)
                .createdBy(AppConstants.SYSTEM_NAME)
                .updatedBy(AppConstants.SYSTEM_NAME)
                .build();

        usersRepository.save(admin);
    }
}