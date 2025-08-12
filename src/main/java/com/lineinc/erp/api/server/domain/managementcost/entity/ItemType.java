package com.lineinc.erp.api.server.domain.managementcost.entity;

import lombok.Getter;

@Getter
public enum ItemType {
    DEPOSIT("보증금"),
    MONTHLY_RENT("월세"),
    MAINTENANCE("관리비(가스/전기/수도)"),
    PARKING_FEE("주차비"),
    MEAL_FEE("식대"),
    KEY_MONEY("전도금"),
    MISC_EXPENSE("기타잡비");

    private final String label;

    ItemType(String label) {
        this.label = label;
    }
}
