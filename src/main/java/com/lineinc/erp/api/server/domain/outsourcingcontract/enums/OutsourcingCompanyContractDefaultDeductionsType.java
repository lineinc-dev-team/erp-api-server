package com.lineinc.erp.api.server.domain.outsourcingcontract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyContractDefaultDeductionsType {
    FOUR_INSURANCES("4대보험"),
    FUEL_COST("유류비"),
    MEAL_COST("식대"),
    ETC("기타");

    private final String label;

    public static String safeLabelOf(String name) {
        try {
            return OutsourcingCompanyContractDefaultDeductionsType.valueOf(name).getLabel();
        } catch (Exception e) {
            return name;
        }
    }

}