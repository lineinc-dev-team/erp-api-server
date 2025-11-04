package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfoSubEquipment;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractSubEquipmentResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유류정보 서브장비 응답")
public record FuelInfoSubEquipmentResponse(
        @Schema(description = "서브장비 ID", example = "1") Long id,
        @Schema(description = "서브장비 정보") ContractSubEquipmentResponse subEquipment,
        @Schema(description = "유종", example = "경유") String fuelType,
        @Schema(description = "유종 코드", example = "DIESEL") String fuelTypeCode,
        @Schema(description = "주유량 (리터)", example = "30") Long fuelAmount,
        @Schema(description = "비고", example = "서브장비 주유") String memo) {

    public static FuelInfoSubEquipmentResponse from(final FuelInfoSubEquipment entity) {
        return new FuelInfoSubEquipmentResponse(
                entity.getId(),
                entity.getOutsourcingCompanyContractSubEquipment() != null
                        ? ContractSubEquipmentResponse.from(entity.getOutsourcingCompanyContractSubEquipment())
                        : null,
                entity.getFuelType() != null ? entity.getFuelType().getLabel() : null,
                entity.getFuelType() != null ? entity.getFuelType().name() : null,
                entity.getFuelAmount(),
                entity.getMemo());
    }

}
