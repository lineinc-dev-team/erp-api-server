package com.lineinc.erp.api.server.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserChangeHistoryType {
    BASIC("기본정보");

    private final String label;
}
