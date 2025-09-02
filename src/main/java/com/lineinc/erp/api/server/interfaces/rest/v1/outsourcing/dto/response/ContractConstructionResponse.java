package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractConstruction;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 공사항목 정보 응답")
public record ContractConstructionResponse(
        @Schema(description = "공사항목 ID", example = "1") Long id,
        @Schema(description = "항목", example = "콘크리트 타설") String item,
        @Schema(description = "규격", example = "C24") String specification,
        @Schema(description = "단위", example = "m³") String unit,
        @Schema(description = "도급단가", example = "50000") Long unitPrice,
        @Schema(description = "도급금액 수량", example = "100") Integer contractQuantity,
        @Schema(description = "도급금액 금액", example = "5000000") Long contractPrice,
        @Schema(description = "외주계약금액 수량", example = "100") Integer outsourcingContractQuantity,
        @Schema(description = "외주계약금액 금액", example = "5000000") Long outsourcingContractPrice,
        @Schema(description = "메모", example = "특수 콘크리트") String memo,
        @Schema(description = "생성일시") OffsetDateTime createdAt,
        @Schema(description = "수정일시") OffsetDateTime updatedAt) {

    public static ContractConstructionResponse from(OutsourcingCompanyContractConstruction construction) {
        return new ContractConstructionResponse(
                construction.getId(),
                construction.getItem(),
                construction.getSpecification(),
                construction.getUnit(),
                construction.getUnitPrice(),
                construction.getContractQuantity(),
                construction.getContractPrice(),
                construction.getOutsourcingContractQuantity(),
                construction.getOutsourcingContractPrice(),
                construction.getMemo(),
                construction.getCreatedAt(),
                construction.getUpdatedAt());
    }
}
