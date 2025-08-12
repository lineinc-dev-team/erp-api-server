package com.lineinc.erp.api.server.domain.worker.enums;

import lombok.Getter;

@Getter
public enum WorkerType {
    REGULAR("정직원"),
    DIRECT("직영"),
    CONTRACTOR("용역"),
    SITE_CONTRACT("현장계약직"),
    ETC("기타");

    private final String label;

    WorkerType(String label) {
        this.label = label;
    }
}
