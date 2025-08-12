package com.lineinc.erp.api.server.domain.outsourcing.enums;

import lombok.Getter;

@Getter
public enum OutsourcingCompanyDefaultDeductionsType {
    FOUR_INSURANCES("4대보험"),
    FUEL_COST("유류비"),
    MEAL_COST("식대"),
    ETC("기타");

    private final String label;

    OutsourcingCompanyDefaultDeductionsType(String label) {
        this.label = label;
    }

    public static String safeLabelOf(String name) {
        try {
            return OutsourcingCompanyDefaultDeductionsType.valueOf(name).getLabel();
        } catch (Exception e) {
            return name;
        }
    }

}