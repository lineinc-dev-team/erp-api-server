package com.lineinc.erp.api.server.domain.managementcost.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ManagementCostChangeHistoryType {

    BASIC("기본정보"),
    MEAL_FEE_REGULAR_EMPLOYEE("직원"),
    MEAL_FEE_DIRECT_CONTRACT("직영"),
    MEAL_FEE_OUTSOURCING("용역"),
    MEAL_FEE_EQUIPMENT("장비"),
    MEAL_FEE_OUTSOURCING_CONTRACT("외주"),
    ITEM_DETAIL("품목상세"),
    KEY_MONEY_DETAIL("전도금 상세"),
    ATTACHMENT("증빙서류");

    private final String label;
}
