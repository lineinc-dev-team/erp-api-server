package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutsourcingCompanyContactSubEquipmentType {
    PIPE_RENTAL("죽통임대"),
    B_K("B/K"),
    BIT_USAGE_FEE("비트손료"),
    ETC("기타");

    private final String label;
}
