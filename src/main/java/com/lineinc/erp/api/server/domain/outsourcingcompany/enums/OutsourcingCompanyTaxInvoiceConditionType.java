package com.lineinc.erp.api.server.domain.outsourcingcompany.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyTaxInvoiceConditionType {
    MONTH_END("월말일괄"),
    QUARTERLY("분기"),
    ON_COMPLETION("완료시");

    private final String label;

}
