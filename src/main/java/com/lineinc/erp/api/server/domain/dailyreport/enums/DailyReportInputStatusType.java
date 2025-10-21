package com.lineinc.erp.api.server.domain.dailyreport.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DailyReportInputStatusType {
    PERSONNEL("인원"),
    EQUIPMENT("장비");

    private final String label;
}
