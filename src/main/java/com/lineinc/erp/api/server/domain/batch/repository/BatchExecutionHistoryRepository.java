package com.lineinc.erp.api.server.domain.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.batch.entity.BatchExecutionHistory;

@Repository
public interface BatchExecutionHistoryRepository extends JpaRepository<BatchExecutionHistory, Long> {
}
