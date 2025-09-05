package com.lineinc.erp.api.server.domain.user.enums;

import lombok.Getter;

@Getter
public enum UserChangeType {
    BASIC("기본정보");

    private final String label;

    UserChangeType(String label) {
        this.label = label;
    }

}
