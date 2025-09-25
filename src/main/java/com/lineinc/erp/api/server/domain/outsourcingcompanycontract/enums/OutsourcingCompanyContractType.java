package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyContractType {
    SERVICE("노무"),
    EQUIPMENT("장비"),
    CONSTRUCTION("외주"),
    ETC("기타");

    private final String label;
}
