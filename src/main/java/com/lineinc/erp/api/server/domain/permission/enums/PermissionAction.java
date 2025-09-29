package com.lineinc.erp.api.server.domain.permission.enums;

import lombok.Getter;

@Getter
public enum PermissionAction {
    VIEW("조회"),
    CREATE("등록"),
    UPDATE("수정"),
    DELETE("삭제"),
    APPROVE("승인");

    private final String label;

    PermissionAction(final String label) {
        this.label = label;
    }

    public static PermissionAction fromLabel(final String label) {
        for (final PermissionAction action : values()) {
            if (action.label.equals(label) || action.name().equals(label)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Unknown permission label: " + label);
    }
}
