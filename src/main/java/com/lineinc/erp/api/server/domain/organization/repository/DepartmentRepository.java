package com.lineinc.erp.api.server.domain.organization.repository;

import com.lineinc.erp.api.server.domain.organization.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
