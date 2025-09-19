package com.lineinc.erp.api.server.domain.labor.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LaborType {
    REGULAR_EMPLOYEE("정직원"),
    DIRECT_CONTRACT("직영/계약직"),
    ETC("기타");

    private final String label;
}
