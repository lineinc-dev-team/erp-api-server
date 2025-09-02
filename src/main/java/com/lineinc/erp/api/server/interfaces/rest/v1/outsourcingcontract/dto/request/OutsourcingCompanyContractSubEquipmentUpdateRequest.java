package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request;

import com.lineinc.erp.api.server.domain.outsourcingcontract.enums.OutsourcingCompanyContactSubEquipmentType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 보조장비 수정 요청")
public record OutsourcingCompanyContractSubEquipmentUpdateRequest(
        @Schema(description = "보조장비 ID", example = "1") Long id,

        @Schema(description = "보조장비 구분", example = "PIPE_RENTAL") OutsourcingCompanyContactSubEquipmentType type,

        @Schema(description = "설명", example = "죽통 임대료") String description,

        @Schema(description = "비고", example = "안전장비 포함") String memo) {
}
