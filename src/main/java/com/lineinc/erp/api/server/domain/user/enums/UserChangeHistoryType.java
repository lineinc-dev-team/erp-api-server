package com.lineinc.erp.api.server.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserChangeHistoryType {
    CREATED("생성"),
    BASIC("기본정보");

    private final String label;
}
