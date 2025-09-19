package com.lineinc.erp.api.server.domain.clientcompany.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClientCompanyFileType {
    BASIC("기본"),
    BUSINESS_LICENSE("사업자등록증");

    private final String label;
}
