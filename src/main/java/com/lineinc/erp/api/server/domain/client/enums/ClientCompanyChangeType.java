package com.lineinc.erp.api.server.domain.client.enums;

import lombok.Getter;

@Getter
public enum ClientCompanyChangeType {
    BASIC("기본 정보"),
    CONTACT("담당자"),
    ATTACHMENT("첨부파일");

    private final String label;

    ClientCompanyChangeType(String label) {
        this.label = label;
    }

}
