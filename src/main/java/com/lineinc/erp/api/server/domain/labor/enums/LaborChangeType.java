package com.lineinc.erp.api.server.domain.labor.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LaborChangeType {
    BASIC("기본 및 추가 정보"),
    ATTACHMENT("첨부파일");

    private final String label;
}
