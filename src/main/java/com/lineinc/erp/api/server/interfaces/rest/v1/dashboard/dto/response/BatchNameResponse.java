package com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response;

import com.lineinc.erp.api.server.domain.batch.enums.BatchName;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "배치 이름 응답")
public record BatchNameResponse(
        @Schema(description = "배치 코드", example = "DASHBOARD_SITE_MONTHLY_COST") String code,
        @Schema(description = "배치명", example = "대시보드 현장 월별 비용 집계 배치") String name) {

    public BatchNameResponse(final BatchName batchName) {
        this(batchName.name(), batchName.getLabel());
    }
}
