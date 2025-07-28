package com.lineinc.erp.api.server.domain.outsourcingcompany.enums;

import lombok.Getter;

@Getter
public enum OutsourcingCompanyType {
    SERVICE("용역"),
    EQUIPMENT("장비"),
    CATERING("식당");

    private final String label;

    OutsourcingCompanyType(String label) {
        this.label = label;
    }
}
