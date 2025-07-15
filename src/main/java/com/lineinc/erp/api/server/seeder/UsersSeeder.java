package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.common.constant.AppConstants;
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
                .username(AppConstants.ROLE_ADMIN_NAME)
                .email(AppConstants.ADMIN_EMAIL)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .createdBy(AppConstants.SYSTEM_NAME)
                .updatedBy(AppConstants.SYSTEM_NAME)
                .build();

        User subAdmin = User.builder()
                .loginId(AppConstants.SUB_ADMIN_LOGIN_ID)
                .username(AppConstants.ROLE_SUB_ADMIN_NAME)
                .email(AppConstants.SUB_ADMIN_EMAIL)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .createdBy(AppConstants.SYSTEM_NAME)
                .updatedBy(AppConstants.SYSTEM_NAME)
                .build();

        User subAdminLite = User.builder()
                .loginId(AppConstants.SUB_ADMIN_LITE_LOGIN_ID)
                .username(AppConstants.ROLE_SUB_ADMIN_WITHOUT_PERMISSION_MENU)
                .email(AppConstants.SUB_ADMIN_LITE_EMAIL)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .createdBy(AppConstants.SYSTEM_NAME)
                .updatedBy(AppConstants.SYSTEM_NAME)
                .build();

        usersRepository.save(admin);
        usersRepository.save(subAdmin);
        usersRepository.save(subAdminLite);
    }
}