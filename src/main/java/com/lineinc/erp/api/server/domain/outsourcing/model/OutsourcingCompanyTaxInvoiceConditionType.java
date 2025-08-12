package com.lineinc.erp.api.server.domain.outsourcing.enums;

import lombok.Getter;

@Getter
public enum OutsourcingCompanyTaxInvoiceConditionType {
    MONTH_END("월말일괄"),
    QUARTERLY("분기"),
    ON_COMPLETION("완료시");

    private final String label;

    OutsourcingCompanyTaxInvoiceConditionType(String label) {
        this.label = label;
    }
}
