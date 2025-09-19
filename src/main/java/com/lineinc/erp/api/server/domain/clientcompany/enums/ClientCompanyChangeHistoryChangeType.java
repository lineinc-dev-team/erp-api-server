package com.lineinc.erp.api.server.domain.clientcompany.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClientCompanyChangeHistoryChangeType {
    BASIC("기본 정보"),
    CONTACT("담당자"),
    ATTACHMENT("첨부파일");

    private final String label;
}
