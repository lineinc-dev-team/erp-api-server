package com.lineinc.erp.api.server.domain.outsourcingcontract.enums;

import lombok.Getter;

@Getter
public enum OutsourcingCompanyContractDriverDocumentType {
    DRIVER_LICENSE("기사자격증"),
    SAFETY_EDUCATION("안전교육"),
    ETC_DOCUMENT("기타서류");

    private final String label;

    OutsourcingCompanyContractDriverDocumentType(String label) {
        this.label = label;
    }
}
