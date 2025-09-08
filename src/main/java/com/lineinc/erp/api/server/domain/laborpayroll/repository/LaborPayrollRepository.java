package com.lineinc.erp.api.server.domain.laborpayroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;

/**
 * 노무비 명세서 Repository
 */
@Repository
public interface LaborPayrollRepository extends JpaRepository<LaborPayroll, Long> {

}
