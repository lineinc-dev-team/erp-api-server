package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractSubEquipment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 부속장비 정보 응답")
public record ContractSubEquipmentResponse(
        @Schema(description = "부속장비 ID", example = "1") Long id,
        @Schema(description = "구분값", example = "죽통임대") String type,
        @Schema(description = "구분값 코드", example = "BIT_USAGE_FEE") String typeCode,
        @Schema(description = "설명", example = "비트손료") String description,
        @Schema(description = "단가", example = "10000") Long unitPrice,
        @Schema(description = "이전단가", example = "9000") Long previousUnitPrice,
        @Schema(description = "작업내용", example = "죽통 임대료") String taskDescription,
        @Schema(description = "메모", example = "특수 부속장비") String memo,
        @Schema(description = "삭제 여부", example = "false") Boolean deleted) {

    public static ContractSubEquipmentResponse from(final OutsourcingCompanyContractSubEquipment subEquipment) {
        return new ContractSubEquipmentResponse(
                subEquipment.getId(),
                subEquipment.getType() != null ? subEquipment.getType().getLabel() : null,
                subEquipment.getType() != null ? subEquipment.getType().name() : null,
                subEquipment.getDescription(),
                subEquipment.getUnitPrice(),
                subEquipment.getPreviousUnitPrice(),
                subEquipment.getTaskDescription(),
                subEquipment.getMemo(),
                subEquipment.isDeleted());
    }
}
