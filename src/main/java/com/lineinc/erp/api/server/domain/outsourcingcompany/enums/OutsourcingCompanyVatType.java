package com.lineinc.erp.api.server.domain.outsourcingcompany.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyVatType {
    NO_VAT("부가세 없음", 0),
    VAT_INCLUDED("부가세 포함", 1),
    VAT_SEPARATE("부가세 별도", 2);

    private final String label;
    private final int order;
}
