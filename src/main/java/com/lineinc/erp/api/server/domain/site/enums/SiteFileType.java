package com.lineinc.erp.api.server.domain.site.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SiteFileType {
    CONTRACT("계약서"),
    DRAWING("현장도면"),
    WARRANTY("보증서류(보증보험)"),
    PERMIT("인허가 서류"),
    ETC("기타");

    private final String label;

}
