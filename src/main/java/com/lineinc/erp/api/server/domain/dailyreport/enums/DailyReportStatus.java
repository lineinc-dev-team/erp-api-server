package com.lineinc.erp.api.server.domain.dailyreport.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DailyReportStatus {
    PENDING("대기"),
    COMPLETED("마감"),
    AUTO_COMPLETED("자동마감");

    private final String label;
}
