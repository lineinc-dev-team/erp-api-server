package com.lineinc.erp.api.server.domain.outsourcingcontract.enums;

import lombok.Getter;

@Getter
public enum OutsourcingCompanyContractChangeType {

    BASIC("기본 정보"),
    CONTACT("담당자"),
    ATTACHMENT("첨부파일"),
    WORKER("인력"),
    EQUIPMENT("장비"),
    DRIVER("기사"),
    CONSTRUCTION("외주공사항목");

    private final String label;

    OutsourcingCompanyContractChangeType(String label) {
        this.label = label;
    }
}
