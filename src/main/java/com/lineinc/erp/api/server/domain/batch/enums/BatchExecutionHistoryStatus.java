package com.lineinc.erp.api.server.domain.batch.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BatchExecutionHistoryStatus {
    RUNNING, // 실행 중
    COMPLETED, // 완료
    FAILED // 실패
}
