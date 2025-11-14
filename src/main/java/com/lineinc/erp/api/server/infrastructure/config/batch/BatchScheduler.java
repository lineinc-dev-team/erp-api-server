package com.lineinc.erp.api.server.infrastructure.config.batch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.batch.entity.BatchExecutionHistory;
import com.lineinc.erp.api.server.domain.batch.enums.BatchExecutionType;
import com.lineinc.erp.api.server.domain.batch.repository.BatchExecutionHistoryRepository;
import com.lineinc.erp.api.server.infrastructure.config.batch.service.BatchService;
import com.lineinc.erp.api.server.infrastructure.config.batch.service.DailyReportAutoCompleteBatchService;
import com.lineinc.erp.api.server.infrastructure.config.batch.service.DashboardSiteMonthlyCostBatchService;
import com.lineinc.erp.api.server.infrastructure.config.batch.service.TenureCalculationBatchService;

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

    private final DailyReportAutoCompleteBatchService dailyReportAutoCompleteBatchService;
    private final TenureCalculationBatchService tenureCalculationBatchService;
    private final DashboardSiteMonthlyCostBatchService dashboardSiteMonthlyCostBatchService;
    private final BatchExecutionHistoryRepository batchExecutionHistoryRepository;

    /**
     * 매일 새벽 00시 1분에 출역일보 자동 마감 배치 실행
     * Cron 표현식: "0 1 0 * * ?" (한국시간 새벽 00시 1분)
     */
    @Scheduled(cron = "0 1 0 * * ?", zone = "Asia/Seoul")
    public void autoCompleteDailyReports() {
        executeBatchWithHistory(dailyReportAutoCompleteBatchService);
    }

    /**
     * 매월 2일 새벽 00시 10분에 근속기간 계산 배치 실행
     * Cron 표현식: "0 10 0 2 * ?" (한국시간 매월 2일 새벽 00시 10분)
     */
    @Scheduled(cron = "0 10 0 2 * ?", zone = "Asia/Seoul")
    public void calculateTenure() {
        executeBatchWithHistory(tenureCalculationBatchService);
    }

    /**
     * 매일 새벽 2시에 대시보드 현장 월별 비용 집계 배치 실행
     * Cron 표현식: "0 0 2 * * ?" (한국시간 매일 새벽 2시 0분)
     */
    @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Seoul")
    public void aggregateDashboardSiteMonthlyCost() {
        executeBatchWithHistory(dashboardSiteMonthlyCostBatchService);
    }

    /**
     * 배치 작업을 실행하고 이력을 기록합니다.
     * 
     * @param batchService 실행할 배치 서비스
     */
    @Transactional
    public void executeBatchWithHistory(final BatchService batchService) {
        final BatchExecutionHistory history = batchService.createExecutionHistory(BatchExecutionType.SCHEDULED);
        batchExecutionHistoryRepository.save(history);

        try {
            log.info("{} 시작", batchService.getBatchName());
            batchService.execute();
            history.markAsCompleted();
            batchExecutionHistoryRepository.save(history);

            log.info("{} 완료 - 실행 시간: {}초",
                    batchService.getBatchName(), history.getExecutionTimeSeconds());
        } catch (final Exception e) {
            history.markAsFailed(e.getMessage());
            batchExecutionHistoryRepository.save(history);

            log.error("{} 실행 중 오류 발생 - 실행 시간: {}초",
                    batchService.getBatchName(), history.getExecutionTimeSeconds(), e);
        }
    }
}
