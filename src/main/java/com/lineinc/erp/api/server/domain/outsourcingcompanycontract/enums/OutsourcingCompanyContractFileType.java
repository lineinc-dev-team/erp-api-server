package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyContractFileType {
    CONTRACT("계약서"),
    GUARANTEE("보증서"),
    BASIC("기본"),
    BUSINESS_REGISTRATION("사업자등록증"),
    BANK_ACCOUNT_COPY("통장사본"),
    EQUIPMENT_REGISTRATION("장비등록증"),
    VEHICLE_INSURANCE("차량보험증권");

    private final String label;
}
