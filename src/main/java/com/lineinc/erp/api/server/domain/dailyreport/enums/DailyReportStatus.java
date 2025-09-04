package com.lineinc.erp.api.server.domain.dailyreport.enums;

import lombok.Getter;

@Getter
public enum DailyReportStatus {
    PENDING("대기"),
    COMPLETED("마감");

    private final String label;

    DailyReportStatus(String label) {
        this.label = label;
    }
}
