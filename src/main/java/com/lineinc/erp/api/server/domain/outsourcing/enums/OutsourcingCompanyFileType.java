package com.lineinc.erp.api.server.domain.outsourcing.enums;

import lombok.Getter;

@Getter
public enum OutsourcingCompanyFileType {
    BUSINESS_LICENSE("사업자등록증"),
    BASIC("기본");

    private final String label;

    OutsourcingCompanyFileType(String label) {
        this.label = label;
    }
}
