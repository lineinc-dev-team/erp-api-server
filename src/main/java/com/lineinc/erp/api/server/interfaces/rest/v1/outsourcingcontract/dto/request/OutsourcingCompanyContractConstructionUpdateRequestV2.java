package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "외주업체 계약 공사항목 수정 요청 V2")
public record OutsourcingCompanyContractConstructionUpdateRequestV2(
        @Schema(description = "그룹 ID", example = "1") Long id,
        @NotBlank @Schema(description = "항목명", example = "콘크리트 타설") String itemName,
        @Schema(description = "공사항목 목록") @Valid List<OutsourcingCompanyContractConstructionUpdateRequest> items) {
}
