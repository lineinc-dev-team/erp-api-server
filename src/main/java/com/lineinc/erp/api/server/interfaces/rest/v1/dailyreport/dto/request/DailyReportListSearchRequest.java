package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;

@ParameterObject
@Schema(description = "출역일보 목록 조회 요청")
public record DailyReportListSearchRequest(
        @Schema(description = "현장명", example = "서울 구내택지 신축공사") String siteName,
        @Schema(description = "공정명", example = "철근공사") String processName,
        @Schema(description = "시작일", example = "2024-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @Schema(description = "종료일", example = "2024-01-31") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
        @Schema(description = "마감여부", example = "true") Boolean isCompleted,
        @Schema(description = "첨부누략여부", example = "true") Boolean isEvidenceSubmitted) {
}
