package com.lineinc.erp.api.server.domain.outsourcingcompany.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyType {
    ETC("기타", 0),
    SERVICE("노무", 1),
    EQUIPMENT("장비", 2),
    CONSTRUCTION("외주", 3),
    MATERIAL("재료", 4),
    FUEL("유류", 5),
    MANAGEMENT("관리", 6),
    RESTAURANT("식당", 7);

    private final String label;
    private final int order;
}
