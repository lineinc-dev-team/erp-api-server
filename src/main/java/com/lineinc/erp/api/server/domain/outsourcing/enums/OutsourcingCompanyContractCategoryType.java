package com.lineinc.erp.api.server.domain.outsourcing.enums;

import lombok.Getter;

@Getter
public enum OutsourcingCompanyContractCategoryType {
    MONTHLY("월대"),
    DAILY("일대"),
    HALF_DAY("반일대");

    private final String label;

    OutsourcingCompanyContractCategoryType(String label) {
        this.label = label;
    }
}
