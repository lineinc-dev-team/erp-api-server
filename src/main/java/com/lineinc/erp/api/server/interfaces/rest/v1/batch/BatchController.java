package com.lineinc.erp.api.server.interfaces.rest.v1.batch;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.infrastructure.config.batch.service.TenureDaysBatchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 배치 작업 관리 컨트롤러
 * 각종 배치 작업을 수동으로 실행할 수 있는 API를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
@Tag(name = "배치 관리", description = "배치 작업 실행 및 관리 API")
public class BatchController {

    private final TenureDaysBatchService tenureDaysBatchService;

    /**
     * 근속일수 업데이트 배치를 수동으로 실행합니다.
     * 
     * @return 배치 실행 결과
     */
    @PostMapping("/tenure-days")
    @Operation(summary = "근속일수 업데이트 배치 실행", description = "모든 인력의 근속일수를 계산하고 업데이트합니다.")
    public ResponseEntity<String> runTenureDaysBatch() {
        try {
            log.info("근속일수 업데이트 배치 수동 실행 시작");
            tenureDaysBatchService.execute();

            String message = "근속일수 업데이트 배치 완료";
            log.info(message);

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            log.error("근속일수 업데이트 배치 실행 중 오류 발생", e);
            return ResponseEntity.status(500)
                    .body("배치 실행 실패: " + e.getMessage());
        }
    }

    // TODO: 다른 배치 작업들을 여기에 추가
    // 예: 월간 리포트 생성, 데이터 정리, 알림 발송 등
}
