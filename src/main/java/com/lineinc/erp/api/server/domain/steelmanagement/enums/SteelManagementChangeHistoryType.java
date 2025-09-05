package com.lineinc.erp.api.server.domain.steelmanagement.enums;

import lombok.Getter;

@Getter
public enum SteelManagementChangeHistoryType {
    BASIC("기본정보"),
    DETAIL("품목상세"),
    ATTACHMENT("첨부파일"),
    OUTSOURCING_COMPANY("거래선");

    private final String label;

    SteelManagementChangeHistoryType(String label) {
        this.label = label;
    }
}
