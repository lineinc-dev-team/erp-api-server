package com.lineinc.erp.api.server.domain.labor.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LaborWorkType {
    ETC("기타", 0),
    OUTSOURCING("용역", 1),
    GAS_WELDER("가스용접공", 2),
    SCAFFOLDING("가시설공", 3);

    private final String label;
    private final int order;
}
