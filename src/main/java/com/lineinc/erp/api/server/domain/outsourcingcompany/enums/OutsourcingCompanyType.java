package com.lineinc.erp.api.server.domain.outsourcingcompany.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyType {
    SERVICE("노무"),
    EQUIPMENT("장비"),
    CONSTRUCTION("외주"),
    MATERIAL("재료"),
    FUEL("유류"),
    MANAGEMENT("관리"),
    ETC("기타");

    private final String label;
}