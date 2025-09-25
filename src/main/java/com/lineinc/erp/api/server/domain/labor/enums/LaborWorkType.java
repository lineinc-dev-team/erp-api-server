package com.lineinc.erp.api.server.domain.labor.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LaborWorkType {
    ETC("기타", 0),
    FOREMAN("반장", 1),
    CARPENTER("목수", 2),
    REBAR("철근", 3),
    SCAFFOLDING("가시설공", 4),
    WELDER("용접공", 5);

    private final String label;
    private final int order;
}
