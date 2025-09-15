package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request;

import java.time.LocalDate;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;

import io.swagger.v3.oas.annotations.media.Schema;

@ParameterObject
@Schema(description = "유류집계 목록 필터 요청")
public record FuelAggregationListRequest(
        @Schema(description = "현장명", example = "서울 APT 신축공사") String siteName,

        @Schema(description = "공정명", example = "기초공사") String processName,

        @Schema(description = "유종 (다중선택)", example = "[\"DIESEL\", \"GASOLINE\"]") List<FuelInfoFuelType> fuelTypes,

        @Schema(description = "업체명", example = "삼성 ENG") String outsourcingCompanyName,

        @Schema(description = "차량번호", example = "12가3456") String vehicleNumber,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Schema(description = "기간 시작일", example = "2025-07-01") LocalDate dateStartDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Schema(description = "기간 종료일", example = "2025-07-31") LocalDate dateEndDate) {
}
