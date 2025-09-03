package com.lineinc.erp.api.server.infrastructure.config.batch.service;

import com.lineinc.erp.api.server.domain.batch.entity.BatchExecutionHistory;

/**
 * 배치 작업 서비스 인터페이스
 * 모든 배치 작업이 구현해야 하는 공통 인터페이스입니다.
 */
public interface BatchService {

    /**
     * 배치 작업을 실행합니다.
     * 
     * @throws Exception 배치 실행 중 발생한 예외
     */
    void execute() throws Exception;

    /**
     * 배치 작업의 이름을 반환합니다.
     * 
     * @return 배치 작업 이름
     */
    String getBatchName();

    /**
     * 배치 실행 이력을 생성합니다.
     * 
     * @return 배치 실행 이력
     */
    default BatchExecutionHistory createExecutionHistory() {
        return BatchExecutionHistory.builder()
                .batchName(getBatchName())
                .startTime(java.time.OffsetDateTime.now())
                .status(BatchExecutionHistory.BatchExecutionStatus.RUNNING)
                .build();
    }
}
