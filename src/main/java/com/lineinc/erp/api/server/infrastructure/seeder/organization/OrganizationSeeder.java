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

        final var newDepartments = AppConstants.DEPARTMENT_NAMES.stream()
                .filter(name -> !existingDepartmentNames.contains(name))
                .map(name -> Department.builder()
                        .name(name)
                        .createdBy(AppConstants.SYSTEM_NAME)
                        .updatedBy(AppConstants.SYSTEM_NAME)
                        .build())
                .collect(Collectors.toList());

        if (!newDepartments.isEmpty()) {
            departmentRepository.saveAll(newDepartments);
        }

        // 직책 시딩 - 없는 것만 추가
        final var existingPositionNames = positionRepository.findAll().stream()
                .map(Position::getName)
                .collect(Collectors.toSet());

        final var newPositions = AppConstants.POSITION_NAMES.stream()
                .filter(name -> !existingPositionNames.contains(name))
                .map(name -> Position.builder()
                        .name(name)
                        .createdBy(AppConstants.SYSTEM_NAME)
                        .updatedBy(AppConstants.SYSTEM_NAME)
                        .build())
                .collect(Collectors.toList());

        if (!newPositions.isEmpty()) {
            positionRepository.saveAll(newPositions);
        }

        // 직급 시딩 - 없는 것만 추가
        final var existingGradeNames = gradeRepository.findAll().stream()
                .map(Grade::getName)
                .collect(Collectors.toSet());

        final var newGrades = AppConstants.GRADE_NAMES.stream()
                .filter(name -> !existingGradeNames.contains(name))
                .map(name -> Grade.builder()
                        .name(name)
                        .createdBy(AppConstants.SYSTEM_NAME)
                        .updatedBy(AppConstants.SYSTEM_NAME)
                        .build())
                .collect(Collectors.toList());

        if (!newGrades.isEmpty()) {
            gradeRepository.saveAll(newGrades);
        }
    }
}