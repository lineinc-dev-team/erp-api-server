package com.lineinc.erp.api.server.domain.outsourcing.enums;

import lombok.Getter;

@Getter
public enum OutsourcingCompanyContractFileType {
    CONTRACT("계약서"),
    GUARANTEE("보증서"),
    BASIC("기본");

    private final String label;

    OutsourcingCompanyContractFileType(String label) {
        this.label = label;
    }
}
