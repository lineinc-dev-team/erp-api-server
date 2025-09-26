package com.lineinc.erp.api.server.interfaces.rest.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.infrastructure.config.batch.service.DailyReportAutoCompleteBatchService;
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
@Tag(name = "배치 관리", description = "배치 작업 실행 및 관리 API")
public class BatchController {

    private final DailyReportAutoCompleteBatchService dailyReportAutoCompleteBatchService;
    private final TenureCalculationBatchService tenureCalculationBatchService;

    /**
     * 출역일보 자동 마감 배치를 수동으로 실행합니다.
     * 
     * @return 배치 실행 결과
     */
    @PostMapping("/daily-report-auto-complete")
    @Operation(summary = "출역일보 자동 마감 배치 실행", description = "전날의 PENDING 상태 출역일보를 AUTO_COMPLETED로 변경합니다.")
    public ResponseEntity<String> runDailyReportAutoCompleteBatch() {
        try {
            log.info("출역일보 자동 마감 배치 수동 실행 시작");
            dailyReportAutoCompleteBatchService.execute();
            final String message = "출역일보 자동 마감 배치 완료";
            log.info(message);
            return ResponseEntity.ok(message);
        } catch (final Exception e) {
            log.error("출역일보 자동 마감 배치 실행 중 오류 발생", e);
            return ResponseEntity.status(500)
                    .body("배치 실행 실패: " + e.getMessage());
        }
    }

    /**
     * 근속기간 계산 배치를 수동으로 실행합니다.
     * 
     * @return 배치 실행 결과
     */
    @PostMapping("/tenure-calculation")
    @Operation(summary = "근속기간 계산 배치 실행", description = "전월 노무비명세서 데이터를 기반으로 각 인력의 근속기간을 계산하고 업데이트합니다.")
    public ResponseEntity<String> runTenureCalculationBatch() {
        try {
            log.info("근속기간 계산 배치 수동 실행 시작");
            tenureCalculationBatchService.execute();
            final String message = "근속기간 계산 배치 완료";
            log.info(message);
            return ResponseEntity.ok(message);
        } catch (final Exception e) {
            log.error("근속기간 계산 배치 실행 중 오류 발생", e);
            return ResponseEntity.status(500)
                    .body("배치 실행 실패: " + e.getMessage());
        }
    }
}
