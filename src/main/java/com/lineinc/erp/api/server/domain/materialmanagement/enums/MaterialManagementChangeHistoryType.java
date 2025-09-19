package com.lineinc.erp.api.server.domain.materialmanagement.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaterialManagementChangeHistoryType {
    BASIC("기본정보"),
    MATERIAL("자재"),
    ATTACHMENT("증빙서류");

    private final String label;
}
