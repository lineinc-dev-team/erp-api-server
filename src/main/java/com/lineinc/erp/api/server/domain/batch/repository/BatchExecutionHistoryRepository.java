package com.lineinc.erp.api.server.domain.batch.repository;

import java.time.OffsetDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.batch.entity.BatchExecutionHistory;

@Repository
public interface BatchExecutionHistoryRepository extends JpaRepository<BatchExecutionHistory, Long> {

    /**
     * 특정 배치 이름과 시간 범위로 실행 이력이 존재하는지 확인
     */
    boolean existsByBatchNameAndStartTimeBetween(String batchName, OffsetDateTime startTime, OffsetDateTime endTime);
}
