package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "외주업체 계약 장비 등록 요청")
public record OutsourcingCompanyContractEquipmentCreateRequest(
        @Schema(description = "규격", example = "20톤 크레인") @NotBlank String specification,

        @Schema(description = "차량번호", example = "12가3456") @NotBlank String vehicleNumber,

        @Schema(description = "장비 구분", example = "크레인") @NotBlank String category,

        @Schema(description = "단가", example = "500000") @NotNull Long unitPrice,

        @Schema(description = "소계", example = "2500000") @NotNull Long subtotal,

        @Schema(description = "작업내용", example = "현장 내 자재 운반 및 설치") String taskDescription,

        @Schema(description = "비고", example = "운전자 포함") String memo,

        @Schema(description = "보조장비 목록") @Valid List<OutsourcingCompanyContractSubEquipmentCreateRequest> subEquipments) {
}
