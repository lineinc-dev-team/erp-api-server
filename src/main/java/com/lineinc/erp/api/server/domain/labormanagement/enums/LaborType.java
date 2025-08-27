package com.lineinc.erp.api.server.domain.labormanagement.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LaborType {
    REGULAR_EMPLOYEE("정직원"),
    DIRECT_CONTRACT("직영/계약직"),
    DIRECT_REGISTRATION("직접등록"),
    ETC("기타");

    private final String label;
}
