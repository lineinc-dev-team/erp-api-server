package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 공사항목 수정 요청")
public record OutsourcingCompanyContractConstructionUpdateRequest(
        @Schema(description = "공사항목 ID", example = "1") Long id,

        @Schema(description = "공사 항목", example = "콘크리트 타설") String item,

        @Schema(description = "규격", example = "C30") String specification,

        @Schema(description = "단위", example = "m³") String unit,

        @Schema(description = "도급단가", example = "50000") Long unitPrice,

        @Schema(description = "도급금액 수량", example = "100") Integer contractQuantity,

        @Schema(description = "도급금액 금액", example = "5000000") Long contractPrice,

        @Schema(description = "외주계약금액 수량", example = "100") Integer outsourcingContractQuantity,

        @Schema(description = "외주계약금액 금액", example = "5000000") Long outsourcingContractPrice,

        @Schema(description = "메모", example = "품질관리 특별요구사항") String memo) {

}
