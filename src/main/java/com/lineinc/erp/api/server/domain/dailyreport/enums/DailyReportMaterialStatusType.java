package com.lineinc.erp.api.server.domain.dailyreport.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DailyReportMaterialStatusType {
    COMPANY_SUPPLIED("사급자재"),
    CLIENT_SUPPLIED("지급자재");

    private final String label;
}
