package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollChangeHistory;
import com.lineinc.erp.api.server.domain.laborpayroll.enums.LaborPayrollChangeType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * 노무명세서 변경이력 응답 DTO
 */
@Schema(description = "노무명세서 변경이력 응답")
public record LaborPayrollChangeHistoryResponse(
        @Schema(description = "변경이력 ID", example = "1") Long id,
        @Schema(description = "변경 상세 내역") @JsonProperty("getChanges") String getChanges,
        @Schema(description = "메모", example = "노무명세서 수정") String memo,
        @Schema(description = "설명", example = "인력 이름과 단가 정보") String description,
        @Schema(description = "생성 일시", example = "2025-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정 일시", example = "2025-01-15T10:00:00+09:00") OffsetDateTime updatedAt,
        @Schema(description = "수정자", example = "관리자") String updatedBy,
        @Schema(description = "변경 유형", example = "노무명세서") String type,
        @Schema(description = "변경 유형 코드", example = "LABOR_PAYROLL") LaborPayrollChangeType typeCode) {

    public static LaborPayrollChangeHistoryResponse from(LaborPayrollChangeHistory history) {
        return new LaborPayrollChangeHistoryResponse(
                history.getId(),
                history.getChanges(),
                history.getMemo(),
                history.getDescription(),
                history.getCreatedAt(),
                history.getUpdatedAt(),
                history.getUpdatedBy(),
                history.getType().getLabel(),
                history.getType());
    }
}
