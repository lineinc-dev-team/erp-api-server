package com.lineinc.erp.api.server.domain.managementcost.enums;

import lombok.Getter;

@Getter
public enum ManagementCostItemType {
    DEPOSIT("보증금"),
    MONTHLY_RENT("월세"),
    UTILITY_GAS("공과금(가스)"),
    UTILITY_ELECTRICITY("공과금(전기)"),
    UTILITY_WATER("공과금(수도)"),
    PARKING_FEE("주차비"),
    MEAL_FEE("식대"),
    KEY_MONEY("전도금"),
    ETC("기타");

    private final String label;

    ManagementCostItemType(final String label) {
        this.label = label;
    }
}
