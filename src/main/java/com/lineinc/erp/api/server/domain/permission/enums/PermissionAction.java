package com.lineinc.erp.api.server.domain.permission.enums;

import lombok.Getter;

@Getter
public enum PermissionAction {
    VIEW("조회"),
    CREATE("등록"),
    UPDATE("수정"),
    DELETE("삭제"),
    EXCEL_DOWNLOAD("엑셀 다운로드"),
    APPROVE("승인");

    private final String label;

    PermissionAction(final String label) {
        this.label = label;
    }

}
