package com.lineinc.erp.api.server.domain.managementcost.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ManagementCostFileType {
    BASIC("기본"),
    UTILITY("공과금");

    private final String label;
}
