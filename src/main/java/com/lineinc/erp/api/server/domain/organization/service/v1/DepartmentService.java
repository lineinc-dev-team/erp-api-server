package com.lineinc.erp.api.server.domain.organization.service.v1;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.repository.DepartmentRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.organization.dto.response.DepartmentResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        final List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .sorted(Comparator.comparing(Department::getId))
                .map(dept -> new DepartmentResponse(dept.getId(), dept.getName()))
                .collect(Collectors.toList());
    }
}
