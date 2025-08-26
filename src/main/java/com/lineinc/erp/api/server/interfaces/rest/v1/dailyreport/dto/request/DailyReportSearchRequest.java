package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.WeatherType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@ParameterObject
@Schema(description = "출역일보 정보 조회 요청")
public record DailyReportSearchRequest(
        @Schema(description = "현장 아이디", example = "1") @NotNull Long siteId,
        @Schema(description = "공정 아이디", example = "1") @NotNull Long siteProcessId,
        @Schema(description = "일자", example = "2024-01-15") @NotNull LocalDate reportDate,
        @Schema(description = "날씨", example = "SUNNY") WeatherType weather) {
}
