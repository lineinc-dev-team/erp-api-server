package com.lineinc.erp.api.server.domain.client.enums;

import lombok.Getter;

/**
 * 발주처 회사 파일 타입
 */
@Getter
public enum FileType {
    BUSINESS_LICENSE("사업자등록증"),
    BASIC("기본");

    private final String label;

    FileType(String label) {
        this.label = label;
    }

}
