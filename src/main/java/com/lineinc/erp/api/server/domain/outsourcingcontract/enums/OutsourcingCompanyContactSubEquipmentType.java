package com.lineinc.erp.api.server.domain.outsourcingcontract.enums;

import lombok.Getter;

@Getter
public enum OutsourcingCompanyContactSubEquipmentType {
    PIPE_RENTAL("죽통임대"),
    B_K("B/K"),
    BIT_USAGE_FEE("비트손료"),
    ETC("기타");

    private final String label;

    OutsourcingCompanyContactSubEquipmentType(String label) {
        this.label = label;
    }
}
