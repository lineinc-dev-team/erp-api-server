package com.lineinc.erp.api.server.domain.laborpayroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollChangeHistory;

/**
 * 노무비 명세서 변경 이력 Repository
 */
@Repository
public interface LaborPayrollChangeHistoryRepository extends JpaRepository<LaborPayrollChangeHistory, Long> {

}
