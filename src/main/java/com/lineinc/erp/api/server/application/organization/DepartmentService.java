package com.lineinc.erp.api.server.application.organization;

import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.presentation.v1.organization.dto.response.DepartmentResponse;
import com.lineinc.erp.api.server.domain.organization.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(dept -> new DepartmentResponse(dept.getId(), dept.getName()))
                .collect(Collectors.toList());
    }
}
