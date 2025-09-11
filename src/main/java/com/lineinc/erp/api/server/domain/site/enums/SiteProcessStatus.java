package com.lineinc.erp.api.server.domain.site.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SiteProcessStatus {
    NOT_STARTED("준비중"),
    IN_PROGRESS("진행중"),
    COMPLETED("종료");

    private final String label;

}