package com.lineinc.erp.api.server.domain.laborpayroll.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LaborPayrollChangeType {
    BASIC("기본 정보"),
    LABOR_PAYROLL("노무비명세서");

    private final String label;
}
