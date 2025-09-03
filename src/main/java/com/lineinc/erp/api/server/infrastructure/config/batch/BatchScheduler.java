package com.lineinc.erp.api.server.infrastructure.config.batch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 배치 작업 스케줄러 테스트
 */
@Slf4j
@Component
public class BatchScheduler {

    /**
     * 3초마다 "hi" 출력
     */
    @Scheduled(fixedRate = 3000)
    public void printHi() {

    }

}
