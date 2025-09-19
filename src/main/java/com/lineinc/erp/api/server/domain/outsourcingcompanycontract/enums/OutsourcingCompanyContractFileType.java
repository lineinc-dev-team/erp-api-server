package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyContractFileType {
    CONTRACT("계약서"),
    GUARANTEE("보증서"),
    BASIC("기본");

    private final String label;
}
