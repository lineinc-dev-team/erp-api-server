package com.lineinc.erp.api.server.domain.outsourcingcompany.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyDefaultDeductionsType {
    FOUR_INSURANCES("4대보험"),
    FUEL_COST("유류비"),
    MEAL_COST("식대"),
    ETC("기타");

    private final String label;

    public static String safeLabelOf(final String name) {
        try {
            return OutsourcingCompanyDefaultDeductionsType.valueOf(name).getLabel();
        } catch (final Exception e) {
            return name;
        }
    }

}