package com.lineinc.erp.api.server.domain.labormanagement.enums;

import lombok.Getter;

/**
 * 노무관리 파일 타입
 */
@Getter
public enum FileType {
    ID_CARD("신분증 사본"),
    BANKBOOK("통장 사본"),
    SIGNATURE_IMAGE("서명이미지"),
    BASIC("기본");

    private final String label;

    FileType(String label) {
        this.label = label;
    }

}
