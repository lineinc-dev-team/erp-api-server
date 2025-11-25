package com.lineinc.erp.api.server.interfaces.rest.common;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lineinc.erp.api.server.domain.batch.entity.BatchExecutionHistory;
import com.lineinc.erp.api.server.domain.batch.enums.BatchExecutionType;
import com.lineinc.erp.api.server.domain.batch.repository.BatchExecutionHistoryRepository;
import com.lineinc.erp.api.server.infrastructure.config.batch.service.BatchService;
import com.lineinc.erp.api.server.infrastructure.config.batch.service.DailyReportAutoCompleteBatchService;
import com.lineinc.erp.api.server.infrastructure.config.batch.service.DashboardSiteMonthlyCostBatchService;
import com.lineinc.erp.api.server.infrastructure.config.batch.service.TenureCalculationBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 배치 작업 관리 컨트롤러
 * 각종 배치 작업을 수동으로 실행할 수 있는 API를 제공합니다.
 * 공통 기능이므로 v1과 독립적으로 배치 기능을 관리합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/common/batch")
@RequiredArgsConstructor
@Tag(name = "배치 관리")
public class BatchController extends BaseController {

    private final DailyReportAutoCompleteBatchService dailyReportAutoCompleteBatchService;
    private final TenureCalculationBatchService tenureCalculationBatchService;
    private final DashboardSiteMonthlyCostBatchService dashboardSiteMonthlyCostBatchService;
    private final BatchExecutionHistoryRepository batchExecutionHistoryRepository;

    /**
     * 출역일보 자동 마감 배치를 수동으로 실행합니다.
     *
     * @return 배치 실행 결과
     */
    @PostMapping("/daily-report-auto-complete")
    @Operation(summary = "출역일보 자동 마감 배치 실행")
    public ResponseEntity<String> runDailyReportAutoCompleteBatch() {
        return executeBatchWithHistory(dailyReportAutoCompleteBatchService);
    }

    /**
     * 근속기간 계산 배치를 수동으로 실행합니다.
     *
     * @return 배치 실행 결과
     */
    @PostMapping("/tenure-calculation")
    @Operation(summary = "근속기간 계산 배치 실행")
    @Transactional
    public ResponseEntity<String> runTenureCalculationBatch() {
        return executeBatchWithHistory(tenureCalculationBatchService);
    }

    /**
     * 대시보드 현장 월별 비용 집계 배치를 수동으로 실행합니다.
     * 대시보드 현장 목록 조회로 반환되는 현장들에 대해 각 월마다 재료비, 노무비, 관리비, 장비비, 외주비를 저장합니다.
     * 기준: 착공일(시작일)이 포함된 월부터 배치 실행 시점의 월까지 계산합니다.
     *
     * @return 배치 실행 결과
     */
    @PostMapping("/dashboard-site-monthly-cost")
    @Operation(summary = "대시보드 현장 월별 비용 집계 배치 실행")
    @Transactional
    public ResponseEntity<String> runDashboardSiteMonthlyCostBatch() {
        return executeBatchWithHistory(dashboardSiteMonthlyCostBatchService);
    }

    /**
     * 배치 작업을 실행하고 이력을 기록합니다.
     *
     * @param batchService 실행할 배치 서비스
     * @return 배치 실행 결과
     */
    private ResponseEntity<String> executeBatchWithHistory(final BatchService batchService) {
        final BatchExecutionHistory history =
                batchService.createExecutionHistory(BatchExecutionType.MANUAL);
        batchExecutionHistoryRepository.save(history);

        try {
            log.info("{} 수동 실행 시작", batchService.getBatchName().getLabel());
            batchService.execute();
            history.markAsCompleted();
            batchExecutionHistoryRepository.save(history);

            final String message = String.format("%s 완료 - 실행 시간: %.2f초",
                    batchService.getBatchName().getLabel(), history.getExecutionTimeSeconds());
            log.info(message);
            return ResponseEntity.ok(message);
        } catch (final Exception e) {
            history.markAsFailed(e.getMessage());
            batchExecutionHistoryRepository.save(history);

            final String errorMessage = String.format("%s 실행 중 오류 발생 - 실행 시간: %.2f초, 오류: %s",
                    batchService.getBatchName().getLabel(), history.getExecutionTimeSeconds(),
                    e.getMessage());
            log.error(errorMessage, e);
            return ResponseEntity.status(500).body("배치 실행 실패: " + e.getMessage());
        }
    }
}
