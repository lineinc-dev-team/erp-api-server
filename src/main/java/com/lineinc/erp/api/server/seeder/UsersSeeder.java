package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.domain.company.entity.Company;
import com.lineinc.erp.api.server.domain.company.repository.CompanyRepository;
import com.lineinc.erp.api.server.domain.users.entity.Users;
import com.lineinc.erp.api.server.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsersSeeder {

    @Value("${ADMIN_LOGIN_ID:admin}")
    private String adminLoginId;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    private final UsersRepository usersRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public void seed() {
        Optional<Users> existingAdmin = usersRepository.findByLoginId(adminLoginId);
        if (existingAdmin.isPresent()) return;

        Company company = companyRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Company with id 1 not found"));

        Users admin = Users.builder()
                .company(company)
                .loginId(adminLoginId)
                .username("관리자")
                .passwordHash(passwordEncoder.encode(adminPassword))
                .build();

        usersRepository.save(admin);
    }
}