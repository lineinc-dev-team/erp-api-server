package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.domain.company.entity.Company;
import com.lineinc.erp.api.server.domain.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class CompanySeeder implements ApplicationRunner {

    private final CompanyRepository companyRepository;

    @Override
    public void run(ApplicationArguments args) {
        String companyName = "라인공영";
        boolean exists = companyRepository.findByName(companyName).isPresent();

        if (!exists) {
            Company company = Company.builder()
                    .name(companyName)
                    .build();

            companyRepository.save(company);
        }
    }
}