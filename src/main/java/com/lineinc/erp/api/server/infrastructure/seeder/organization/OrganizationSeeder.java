package com.lineinc.erp.api.server.infrastructure.seeder.organization;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.entity.Position;
import com.lineinc.erp.api.server.domain.organization.repository.DepartmentRepository;
import com.lineinc.erp.api.server.domain.organization.repository.GradeRepository;
import com.lineinc.erp.api.server.domain.organization.repository.PositionRepository;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrganizationSeeder {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final GradeRepository gradeRepository;

    @Transactional
    public void seed() {
        // 부서 시딩 - 없는 것만 추가
        final var existingDepartmentNames = departmentRepository.findAll().stream()
                .map(Department::getName)
                .collect(Collectors.toSet());

        int departmentOrder = 1;
        for (final String name : AppConstants.DEPARTMENT_NAMES) {
            if (!existingDepartmentNames.contains(name)) {
                final Department department = Department.builder()
                        .name(name)
                        .order(departmentOrder)
                        .createdBy(AppConstants.SYSTEM_NAME)
                        .updatedBy(AppConstants.SYSTEM_NAME)
                        .build();
                departmentRepository.save(department);
            }
            departmentOrder++;
        }

        // 직책 시딩 - 없는 것만 추가
        final var existingPositionNames = positionRepository.findAll().stream()
                .map(Position::getName)
                .collect(Collectors.toSet());

        int positionOrder = 1;
        for (final String name : AppConstants.POSITION_NAMES) {
            if (!existingPositionNames.contains(name)) {
                final Position position = Position.builder()
                        .name(name)
                        .order(positionOrder)
                        .createdBy(AppConstants.SYSTEM_NAME)
                        .updatedBy(AppConstants.SYSTEM_NAME)
                        .build();
                positionRepository.save(position);
            }
            positionOrder++;
        }

        // 직급 시딩 - 없는 것만 추가
        final var existingGradeNames = gradeRepository.findAll().stream()
                .map(Grade::getName)
                .collect(Collectors.toSet());

        int gradeOrder = 1;
        for (final String name : AppConstants.GRADE_NAMES) {
            if (!existingGradeNames.contains(name)) {
                final Grade grade = Grade.builder()
                        .name(name)
                        .order(gradeOrder)
                        .createdBy(AppConstants.SYSTEM_NAME)
                        .updatedBy(AppConstants.SYSTEM_NAME)
                        .build();
                gradeRepository.save(grade);
            }
            gradeOrder++;
        }
    }
}