package com.lineinc.erp.api.server.infrastructure.config.batch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    /**
     * 매일 새벽 1시에 근속일수 업데이트 배치 실행
     * Cron 표현식: "0 0 1 * * ?" (한국시간 새벽 1시)
     */
    @Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Seoul")
    public void updateTenureDays() {
        try {
            tenureDaysBatchService.execute();
        } catch (Exception e) {
            log.error("근속일수 업데이트 배치 실행 중 오류 발생", e);
        }
    }

    // TODO: 다른 배치 작업들을 여기에 추가
}
