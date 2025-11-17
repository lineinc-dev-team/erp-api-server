package com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "대시보드 현장 비용 집계 배치 실행 시간 응답")
public record DashboardBatchExecutionTimeResponse(
        @Schema(description = "가장 최근 배치 실행 종료 시간", example = "2024-01-15T10:30:00+09:00")
        OffsetDateTime lastExecutionTime) {
}

