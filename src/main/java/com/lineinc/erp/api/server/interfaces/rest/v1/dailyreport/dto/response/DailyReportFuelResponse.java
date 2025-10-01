package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractDriverResponse.ContractDriverSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractEquipmentResponse.ContractEquipmentSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 유류 응답")
public record DailyReportFuelResponse(
        @Schema(description = "유류집계 ID", example = "1") Long fuelAggregationId,
        @Schema(description = "유류집계 info id", example = "1") Long fuelInfoId,
        @Schema(description = "유종", example = "경유") String fuelType,
        @Schema(description = "유종 타입 코드", example = "DIESEL") FuelInfoFuelType fuelTypeCode,
        @Schema(description = "주유량", example = "100") Long fuelAmount,
        @Schema(description = "비고", example = "오전 주유") String memo,
        @Schema(description = "사진 URL", example = "https://example.com/photo.jpg") String fileUrl,
        @Schema(description = "사진 원본 파일명", example = "photo.jpg") String originalFileName,
        @Schema(description = "업체 정보") CompanySimpleResponse outsourcingCompany,
        @Schema(description = "기사 정보") ContractDriverSimpleResponse outsourcingCompanyDriver,
        @Schema(description = "장비 정보") ContractEquipmentSimpleResponse outsourcingCompanyEquipment,
        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportFuelResponse from(final FuelInfo fuelInfo) {
        return new DailyReportFuelResponse(
                fuelInfo.getFuelAggregation() != null ? fuelInfo.getFuelAggregation().getId() : null,
                fuelInfo.getId(),
                fuelInfo.getFuelType() != null ? fuelInfo.getFuelType().getLabel() : null,
                fuelInfo.getFuelType() != null ? fuelInfo.getFuelType() : null,
                fuelInfo.getFuelAmount(),
                fuelInfo.getMemo(),
                fuelInfo.getFileUrl(),
                fuelInfo.getOriginalFileName(),
                fuelInfo.getOutsourcingCompany() != null
                        ? CompanySimpleResponse.from(fuelInfo.getOutsourcingCompany())
                        : null,
                fuelInfo.getDriver() != null
                        ? ContractDriverSimpleResponse.from(fuelInfo.getDriver())
                        : null,
                fuelInfo.getEquipment() != null
                        ? ContractEquipmentSimpleResponse.from(fuelInfo.getEquipment())
                        : null,
                fuelInfo.getCreatedAt(),
                fuelInfo.getUpdatedAt());
    }
}
