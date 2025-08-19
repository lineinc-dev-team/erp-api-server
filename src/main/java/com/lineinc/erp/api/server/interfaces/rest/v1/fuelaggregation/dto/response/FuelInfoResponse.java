package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유류정보 응답")
public record FuelInfoResponse(
        @Schema(description = "유류정보 ID", example = "1") Long id,
        @Schema(description = "업체명", example = "ABC 운송") String companyName,
        @Schema(description = "기사명", example = "홍길동") String driverName,
        @Schema(description = "장비 정보", example = "덤프트럭 (12톤)") String equipmentInfo,
        @Schema(description = "유종", example = "휘발유") String fuelType,
        @Schema(description = "유종 코드", example = "GASOLINE") String fuelTypeCode,
        @Schema(description = "주유량 (리터)", example = "50") Long fuelAmount,
        @Schema(description = "비고", example = "오전 주유") String memo) {

    public static FuelInfoResponse from(FuelInfo entity) {
        String equipmentInfo = null;
        if (entity.getEquipment() != null) {
            if (entity.getEquipment().getVehicleNumber() != null && entity.getEquipment().getSpecification() != null) {
                equipmentInfo = entity.getEquipment().getSpecification() + " ("
                        + entity.getEquipment().getVehicleNumber() + ")";
            } else if (entity.getEquipment().getSpecification() != null) {
                equipmentInfo = entity.getEquipment().getSpecification();
            } else if (entity.getEquipment().getVehicleNumber() != null) {
                equipmentInfo = entity.getEquipment().getVehicleNumber();
            }
        }

        return new FuelInfoResponse(
                entity.getId(),
                entity.getOutsourcingCompany() != null ? entity.getOutsourcingCompany().getName() : null,
                entity.getDriver() != null ? entity.getDriver().getName() : null,
                equipmentInfo,
                entity.getFuelType() != null ? entity.getFuelType().getLabel() : null,
                entity.getFuelType() != null ? entity.getFuelType().name() : null,
                entity.getFuelAmount(),
                entity.getMemo());
    }
}
