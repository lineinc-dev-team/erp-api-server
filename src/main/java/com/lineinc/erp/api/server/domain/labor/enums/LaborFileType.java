package com.lineinc.erp.api.server.domain.labor.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LaborFileType {
    ID_CARD("신분증 사본"),
    BANKBOOK("통장 사본"),
    SIGNATURE_IMAGE("서명이미지"),
    BASIC("기본");

    private final String label;
}
