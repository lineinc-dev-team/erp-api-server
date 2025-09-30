package com.lineinc.erp.api.server.domain.dailyreport.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DailyReportEvidenceFileType {
    EMPLOYEE("직원"),
    DIRECT_CONTRACT("직영/계약직"),
    OUTSOURCING("외주"),
    EQUIPMENT("장비"),
    FUEL("유류");

    private final String label;
}
