package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyContractCategoryType {
    MONTHLY("월대"), DAILY("일대"), HALF_DAY("반일대"), NOT_ISSUED("발행안함");

    private final String label;
}
