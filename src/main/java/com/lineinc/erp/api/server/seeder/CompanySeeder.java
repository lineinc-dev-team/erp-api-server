package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.domain.company.entity.Company;
import com.lineinc.erp.api.server.domain.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanySeeder {

    private final CompanyRepository companyRepository;

    public void seed() {
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