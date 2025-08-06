package com.lineinc.erp.api.server.domain.outsourcing.enums;

import lombok.Getter;

@Getter
public enum OutsourcingChangeType {
    BASIC("기본 정보"),
    CONTACT("담당자"),
    ATTACHMENT("첨부파일");

    private final String label;

    OutsourcingChangeType(String label) {
        this.label = label;
    }
}
