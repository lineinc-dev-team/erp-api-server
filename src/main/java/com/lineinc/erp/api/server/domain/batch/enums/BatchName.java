package com.lineinc.erp.api.server.domain.batch.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 배치 이름 Enum
 */
@Getter
@RequiredArgsConstructor
public enum BatchName {
    DAILY_REPORT_AUTO_COMPLETE("출역일보 자동 마감 배치"),
    DASHBOARD_SITE_MONTHLY_COST("대시보드 현장 월별 비용 집계 배치"),
    SEVERANCE_PAY_ELIGIBILITY_CALCULATION("퇴직금 발생 여부 계산 배치");

    private final String label;
}
