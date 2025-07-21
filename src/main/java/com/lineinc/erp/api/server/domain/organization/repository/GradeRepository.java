package com.lineinc.erp.api.server.domain.organization.repository;

import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
}
