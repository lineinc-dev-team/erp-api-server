package com.lineinc.erp.api.server.domain.permission.enums;

import lombok.Getter;

@Getter
public enum PermissionAction {
    CREATE("등록", 1),
    VIEW("조회", 2),
    UPDATE("수정", 3),
    DELETE("삭제", 4),
    EXCEL_DOWNLOAD("엑셀 다운로드", 5),
    APPROVE("승인", 6);

    private final String label;
    private final int order;

    PermissionAction(final String label, final int order) {
        this.label = label;
        this.order = order;
    }

}
