package com.lineinc.erp.api.server.domain.labormanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.labormanagement.entity.LaborChangeHistory;

@Repository
public interface LaborChangeHistoryRepository extends JpaRepository<LaborChangeHistory, Long> {
}
