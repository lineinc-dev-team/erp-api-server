package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@ParameterObject
@Schema(description = "유류집계 엑셀 다운로드 요청")
public record FuelAggregationDownloadRequest(
        @NotBlank @Schema(description = "허용 필드: id, siteName, processName, date, outsourcingCompanyName, driverName, vehicleNumber, specification, fuelType, fuelAmount, createdAtAndUpdatedAt, memo", example = "id, siteName, fuelType") String fields) {

    public static final List<String> ALLOWED_FIELDS = List.of(
            "id", "siteName", "processName", "date", "outsourcingCompanyName", "driverName",
            "vehicleNumber", "specification", "fuelType", "fuelAmount", "createdAtAndUpdatedAt", "memo");
}
