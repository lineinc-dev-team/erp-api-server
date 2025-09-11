package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력명 응답")
public record LaborNameResponse(
        @Schema(description = "인력 ID", example = "1") Long id,
        @Schema(description = "인력명", example = "홍길동") String name,
        @Schema(description = "기준일당", example = "100000") Long dailyWage,
        @Schema(description = "이전단가", example = "95000") Long previousDailyWage,
        @Schema(description = "노무인력 유형", example = "정직원") String type,
        @Schema(description = "임시 인력 여부", example = "false") Boolean isTemporary,
        @Schema(description = "노무인력 유형 코드", example = "REGULAR_EMPLOYEE") LaborType typeCode) {

    public static LaborNameResponse from(Labor labor) {
        return new LaborNameResponse(
                labor.getId(),
                labor.getName(),
                labor.getDailyWage(),
                labor.getPreviousDailyWage(),
                labor.getType() != null ? labor.getType().getLabel() : null,
                labor.getIsTemporary(),
                labor.getType());
    }
}
