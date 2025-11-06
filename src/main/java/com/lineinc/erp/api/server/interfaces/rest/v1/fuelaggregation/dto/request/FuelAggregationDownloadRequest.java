package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@ParameterObject
@Schema(description = "유류집계 엑셀 다운로드 요청")
public record FuelAggregationDownloadRequest(
        @NotBlank @Schema(description = "다운로드할 필드들을 쉼표로 구분", example = EXAMPLE_FIELDS) String fields) {
    private static final String EXAMPLE_FIELDS = "id,siteName,processName,date,outsourcingCompanyName,driverName,vehicleNumber,specification,fuelType,fuelAmount,amount,createdAtAndUpdatedAt,memo";
    public static final List<String> ALLOWED_FIELDS = List.of(EXAMPLE_FIELDS.split(","));
}
