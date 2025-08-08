package com.lineinc.erp.api.server.seeder;


import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.entity.Position;
import com.lineinc.erp.api.server.domain.organization.repository.DepartmentRepository;
import com.lineinc.erp.api.server.domain.organization.repository.GradeRepository;
import com.lineinc.erp.api.server.domain.organization.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrganizationSeeder {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final GradeRepository gradeRepository;

    @Transactional
    public void seed() {
        if (departmentRepository.count() == 0) {
            var departments = AppConstants.DEPARTMENT_NAMES.stream()
                    .map(name -> Department.builder()
                            .name(name)
                            .createdBy(AppConstants.SYSTEM_NAME)
                            .updatedBy(AppConstants.SYSTEM_NAME)
                            .build())
                    .collect(Collectors.toList());
            departmentRepository.saveAll(departments);
        }

        if (positionRepository.count() == 0) {
            var positions = AppConstants.POSITION_NAMES.stream()
                    .map(name -> Position.builder()
                            .name(name)
                            .createdBy(AppConstants.SYSTEM_NAME)
                            .updatedBy(AppConstants.SYSTEM_NAME)
                            .build())
                    .collect(Collectors.toList());
            positionRepository.saveAll(positions);
        }

        if (gradeRepository.count() == 0) {
            var grades = AppConstants.GRADE_NAMES.stream()
                    .map(name -> Grade.builder()
                            .name(name)
                            .createdBy(AppConstants.SYSTEM_NAME)
                            .updatedBy(AppConstants.SYSTEM_NAME)
                            .build())
                    .collect(Collectors.toList());
            gradeRepository.saveAll(grades);
        }
    }
}