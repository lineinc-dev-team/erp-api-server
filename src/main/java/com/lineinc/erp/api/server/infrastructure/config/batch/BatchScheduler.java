package com.lineinc.erp.api.server.infrastructure.config.batch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.batch.entity.BatchExecutionHistory;
import com.lineinc.erp.api.server.domain.batch.repository.BatchExecutionHistoryRepository;
import com.lineinc.erp.api.server.infrastructure.config.batch.service.BatchService;
import com.lineinc.erp.api.server.infrastructure.config.batch.service.TenureDaysBatchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 배치 작업 스케줄러
 * 정해진 시간에 각종 배치 작업을 자동으로 실행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final TenureDaysBatchService tenureDaysBatchService;
    private final BatchExecutionHistoryRepository batchExecutionHistoryRepository;

    /**
     * 매일 새벽 1시에 근속일수 업데이트 배치 실행
     * Cron 표현식: "0 0 1 * * ?" (한국시간 새벽 1시)
     */
    @Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Seoul")
    public void updateTenureDays() {
        executeBatchWithHistory(tenureDaysBatchService);
    }

    /**
     * 배치 작업을 실행하고 이력을 기록합니다.
     * 
     * @param batchService 실행할 배치 서비스
     */
    @Transactional
    public void executeBatchWithHistory(BatchService batchService) {
        BatchExecutionHistory history = batchService.createExecutionHistory();
        batchExecutionHistoryRepository.save(history);

        try {
            log.info("{} 시작", batchService.getBatchName());
            batchService.execute();
            history.markAsCompleted();
            batchExecutionHistoryRepository.save(history);

            log.info("{} 완료 - 실행 시간: {}초",
                    batchService.getBatchName(), history.getExecutionTimeSeconds());
        } catch (Exception e) {
            history.markAsFailed(e.getMessage());
            batchExecutionHistoryRepository.save(history);

            log.error("{} 실행 중 오류 발생 - 실행 시간: {}초",
                    batchService.getBatchName(), history.getExecutionTimeSeconds(), e);
        }
    }

    // TODO: 다른 배치 작업들을 여기에 추가
}
