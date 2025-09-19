package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyContractDriverDocumentType {
    DRIVER_LICENSE("기사자격증"),
    SAFETY_EDUCATION("안전교육"),
    ETC_DOCUMENT("기타서류");

    private final String label;
}
