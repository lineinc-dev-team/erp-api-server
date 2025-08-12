package com.lineinc.erp.api.server.seeder.user;

import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsersSeeder {

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public void seed() {
        Optional<User> existingAdmin = usersRepository.findByLoginId(AppConstants.ADMIN_LOGIN_ID);
        if (existingAdmin.isPresent()) return;

        User admin = User.builder()
                .loginId(AppConstants.ADMIN_LOGIN_ID)
                .username(AppConstants.ADMIN_USERNAME)
                .email(AppConstants.ADMIN_EMAIL)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .requirePasswordReset(false)
                .createdBy(AppConstants.SYSTEM_NAME)
                .updatedBy(AppConstants.SYSTEM_NAME)
                .build();

        usersRepository.save(admin);
    }
}