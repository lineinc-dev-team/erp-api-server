package com.lineinc.erp.api.server.domain.outsourcingcontract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyContractStatus {
    IN_PROGRESS("진행중"),
    COMPLETED("종료"),
    ON_HOLD("보류");

    private final String label;
}
