package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractEquipment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 장비 정보 응답")
public record ContractEquipmentResponse(
        @Schema(description = "장비 ID", example = "1") Long id,
        @Schema(description = "규격", example = "25톤 크레인") String specification,
        @Schema(description = "차량번호", example = "12가3456") String vehicleNumber,
        @Schema(description = "카테고리", example = "크레인") String category,
        @Schema(description = "단가", example = "500000") Long unitPrice,
        @Schema(description = "소계", example = "5000000") Long subtotal,
        @Schema(description = "작업내용", example = "강재 운반 및 설치") String taskDescription,
        @Schema(description = "비고", example = "특수장비 운전자 필요") String memo,
        @Schema(description = "생성일시") OffsetDateTime createdAt,
        @Schema(description = "수정일시") OffsetDateTime updatedAt,
        @Schema(description = "부속장비 목록") List<ContractSubEquipmentResponse> subEquipments) {

    public static ContractEquipmentResponse from(OutsourcingCompanyContractEquipment equipment) {
        return new ContractEquipmentResponse(
                equipment.getId(),
                equipment.getSpecification(),
                equipment.getVehicleNumber(),
                equipment.getCategory(),
                equipment.getUnitPrice(),
                equipment.getSubtotal(),
                equipment.getTaskDescription(),
                equipment.getMemo(),
                equipment.getCreatedAt(),
                equipment.getUpdatedAt(),
                equipment.getSubEquipments() != null ? equipment.getSubEquipments().stream()
                        .map(ContractSubEquipmentResponse::from)
                        .toList() : List.of());
    }

    @Schema(description = "외주업체 계약 장비 간단 정보 응답")
    public record ContractEquipmentSimpleResponse(
            @Schema(description = "장비 ID", example = "1") Long id,
            @Schema(description = "규격", example = "25톤 크레인") String specification,
            @Schema(description = "차량번호", example = "12가3456") String vehicleNumber,
            @Schema(description = "카테고리", example = "크레인") String category) {

        public static ContractEquipmentSimpleResponse from(OutsourcingCompanyContractEquipment equipment) {
            return new ContractEquipmentSimpleResponse(
                    equipment.getId(),
                    equipment.getSpecification(),
                    equipment.getVehicleNumber(),
                    equipment.getCategory());

        }
    }
}
