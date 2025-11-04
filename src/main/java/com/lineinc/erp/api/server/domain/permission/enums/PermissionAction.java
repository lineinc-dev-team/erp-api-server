package com.lineinc.erp.api.server.domain.permission.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionAction {
    CREATE("등록", 1),
    VIEW("조회", 2),
    UPDATE("수정", 3),
    DELETE("삭제", 4),
    EXCEL_DOWNLOAD("엑셀 다운로드", 5),
    APPROVE("승인", 6);

    private final String label;
    private final int order;

}
