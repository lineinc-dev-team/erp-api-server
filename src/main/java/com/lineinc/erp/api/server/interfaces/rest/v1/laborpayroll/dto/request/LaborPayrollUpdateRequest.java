package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

/**
 * 노무명세서 수정 요청 DTO
 */
@Schema(description = "노무명세서 수정 요청")
@Builder
public record LaborPayrollUpdateRequest(
        @Schema(description = "노무명세서 수정 정보 목록") List<LaborPayrollInfo> laborPayrollInfos) {
}
