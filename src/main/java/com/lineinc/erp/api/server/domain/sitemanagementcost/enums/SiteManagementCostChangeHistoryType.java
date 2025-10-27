package com.lineinc.erp.api.server.domain.sitemanagementcost.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SiteManagementCostChangeHistoryType {
    SITE_MANAGEMENT_COST("현장관리비"),
    HEADQUARTERS_MANAGEMENT_COST("본사관리비");

    private final String label;
}
