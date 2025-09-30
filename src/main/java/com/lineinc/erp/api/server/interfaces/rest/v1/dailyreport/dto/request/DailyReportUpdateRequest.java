package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import java.util.List;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@Schema(description = "출역일보 수정 요청")
public record DailyReportUpdateRequest(
        @Schema(description = "날씨", example = "SUNNY") FuelAggregationWeatherType weather,
        @Schema(description = "증빙 파일 목록") @Valid List<DailyReportEvidenceFileUpdateRequest> evidenceFiles) {
}
