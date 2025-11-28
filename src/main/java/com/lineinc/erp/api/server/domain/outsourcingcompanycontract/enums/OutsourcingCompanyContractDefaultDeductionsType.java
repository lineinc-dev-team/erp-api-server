package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyContractDefaultDeductionsType {
    FUEL_COST("유류비"),
    MEAL_COST("식대");

    private final String label;

    public static String safeLabelOf(
            final String name) {
        try {
            return OutsourcingCompanyContractDefaultDeductionsType.valueOf(name).getLabel();
        } catch (final Exception e) {
            return name;
        }
    }

}
