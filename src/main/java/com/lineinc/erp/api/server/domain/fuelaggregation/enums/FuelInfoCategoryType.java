package com.lineinc.erp.api.server.domain.fuelaggregation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FuelInfoCategoryType {
    EQUIPMENT("장비"),
    CONSTRUCTION("외주");

    private final String label;
}
