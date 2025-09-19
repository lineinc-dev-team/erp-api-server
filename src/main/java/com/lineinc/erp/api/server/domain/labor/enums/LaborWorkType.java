package com.lineinc.erp.api.server.domain.labor.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LaborWorkType {
    FOREMAN("반장"),
    CARPENTER("목수"),
    REBAR("철근"),
    SCAFFOLDING("가시설공"),
    WELDER("용접공"),
    ETC("기타");

    private final String label;
}
