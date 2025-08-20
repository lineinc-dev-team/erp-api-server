package com.lineinc.erp.api.server.domain.labormanagement.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LaborType {
    REGULAR_EMPLOYEE("정직원"),
    DIRECT_EMPLOYEE("직영"),
    OUTSOURCING("용역"),
    SITE_CONTRACT("현장계약직"),
    ETC("기타");

    private final String label;
}
