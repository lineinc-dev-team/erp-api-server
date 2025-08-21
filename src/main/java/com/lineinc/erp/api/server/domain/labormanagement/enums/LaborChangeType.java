package com.lineinc.erp.api.server.domain.labormanagement.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LaborChangeType {
    BASIC("기본정보"),
    ATTACHMENT("첨부파일");

    private final String label;
}
