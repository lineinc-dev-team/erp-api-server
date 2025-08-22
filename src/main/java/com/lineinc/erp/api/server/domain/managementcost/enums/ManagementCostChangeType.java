package com.lineinc.erp.api.server.domain.managementcost.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ManagementCostChangeType {

    BASIC("기본정보"),
    MEAL_FEE("식대"),
    ITEM_DETAIL("품목상세"),
    KEY_MONEY_DETAIL("전도금 상세"),
    ATTACHMENT("증빙서류");

    private final String label;
}
