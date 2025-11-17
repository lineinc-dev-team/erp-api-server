package com.lineinc.erp.api.server.domain.batch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.batch.entity.BatchExecutionHistory;
import com.lineinc.erp.api.server.domain.batch.enums.BatchName;

@Repository
public interface BatchExecutionHistoryRepository extends JpaRepository<BatchExecutionHistory, Long> {

    /**
     * 특정 배치 이름의 가장 최근 실행 종료 시간 조회
     * 
     * @param batchName 배치 이름 Enum
     * @return 가장 최근 BatchExecutionHistory (없으면 Optional.empty())
     */
    Optional<BatchExecutionHistory> findTop1ByBatchNameAndEndTimeIsNotNullOrderByEndTimeDesc(BatchName batchName);
}
