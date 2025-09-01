package com.lineinc.erp.api.server.domain.outsourcing.enums;

import lombok.Getter;

@Getter
public enum OutsourcingCompanyType {
    SERVICE("용역"),
    EQUIPMENT("장비"),
    CONSTRUCTION("공사"),
    ETC("기타");

    private final String label;

    OutsourcingCompanyType(String label) {
        this.label = label;
    }
}