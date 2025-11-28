package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyContractChangeType {
    BASIC("기본 정보"),
    CONTACT("담당자"),
    ATTACHMENT("첨부파일"),
    WORKER("인력"),
    EQUIPMENT("장비"),
    DRIVER("기사"),
    CONSTRUCTION("외주공사항목");

    private final String label;
}
