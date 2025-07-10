package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.domain.company.entity.Company;
import com.lineinc.erp.api.server.domain.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanySeeder {

    private final CompanyRepository companyRepository;

    public void seed() {
        boolean exists = companyRepository.findByName(AppConstants.COMPANY_MAIN_NAME).isPresent();

        if (!exists) {
            Company company = Company.builder()
                    .name(AppConstants.COMPANY_MAIN_NAME)
                    .createdBy(AppConstants.SYSTEM_NAME)
                    .updatedBy(AppConstants.SYSTEM_NAME)
                    .build();

            companyRepository.save(company);
        }
    }
}