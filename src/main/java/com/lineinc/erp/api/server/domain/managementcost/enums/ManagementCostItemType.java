package com.lineinc.erp.api.server.domain.managementcost.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ManagementCostItemType {
    ETC("기타", 0),
    DEPOSIT("보증금", 1),
    MONTHLY_RENT("월세", 2),
    UTILITY_GAS("공과금(가스)", 3),
    UTILITY_ELECTRICITY("공과금(전기)", 4),
    UTILITY_WATER("공과금(수도)", 5),
    PARKING_FEE("주차비", 6),
    MEAL_FEE("식대", 7),
    KEY_MONEY("전도금", 8),
    SNACK_FEE("간식비", 9);

    private final String label;
    private final int order;
}
