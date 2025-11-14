package com.lineinc.erp.api.server.domain.batch.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 배치 실행 타입
 */
@Getter
@RequiredArgsConstructor
public enum BatchExecutionType {
    SCHEDULED("자동 실행"), // 스케줄러를 통한 자동 실행
    MANUAL("수동 실행"); // 수동 실행

    private final String label;
}
