package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.domain.company.entity.Company;
import com.lineinc.erp.api.server.domain.company.repository.CompanyRepository;
import com.lineinc.erp.api.server.domain.users.entity.Users;
import com.lineinc.erp.api.server.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Order(4)
public class UsersSeeder implements ApplicationRunner {

    @Value("${ADMIN_LOGIN_ID:admin}")
    private String adminLoginId;

    @Value("${ADMIN_PASSWORD_HASH}")
    private String adminPasswordHash;

    private final UsersRepository usersRepository;
    private final CompanyRepository companyRepository;

    @Override
    public void run(ApplicationArguments args) {

        Optional<Users> existingAdmin = usersRepository.findByLoginId(adminLoginId);
        if (existingAdmin.isPresent()) return;

        Company company = companyRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Company with id 1 not found"));

        Users admin = Users.builder()
                .company(company)
                .loginId(adminLoginId)
                .username("관리자")
                .passwordHash(adminPasswordHash)
                .build();

        usersRepository.save(admin);
    }
}