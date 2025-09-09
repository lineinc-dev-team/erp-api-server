package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 노무명세서 집계 테이블 수정 요청 DTO
 */
@Schema(description = "노무명세서 집계 테이블 수정 요청")
@Builder
public record LaborPayrollSummaryUpdateRequest(
        @Schema(description = "집계 비고", example = "2024년 1월 노무비 집계 비고") String memo) {
}
