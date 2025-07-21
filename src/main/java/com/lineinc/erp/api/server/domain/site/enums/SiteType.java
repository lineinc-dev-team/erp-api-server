package com.lineinc.erp.api.server.domain.site.enums;

import lombok.Getter;

@Getter
public enum SiteType {
    CONSTRUCTION("건축"),
    CIVIL_ENGINEERING("토목"),
    OUTSOURCING("외주");

    private final String label;

    SiteType(String label) {
        this.label = label;
    }
}
