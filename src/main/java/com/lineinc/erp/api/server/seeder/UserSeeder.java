package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.domain.company.entity.Company;
import com.lineinc.erp.api.server.domain.company.repository.CompanyRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserSeeder {

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    private final UserRepository usersRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public void seed() {
        Optional<User> existingAdmin = usersRepository.findByLoginId(AppConstants.ADMIN_LOGIN_ID);
        if (existingAdmin.isPresent()) return;

        Optional<Company> companyOpt = companyRepository.findByName(AppConstants.COMPANY_MAIN_NAME);
        if (companyOpt.isEmpty()) {
            return;
        }
        Company company = companyOpt.get();

        User admin = User.builder()
                .company(company)
                .loginId(AppConstants.ADMIN_LOGIN_ID)
                .username(AppConstants.ROLE_MASTER_NAME)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .createdBy(AppConstants.SYSTEM_NAME)
                .updatedBy(AppConstants.SYSTEM_NAME)
                .build();

        usersRepository.save(admin);
    }
}