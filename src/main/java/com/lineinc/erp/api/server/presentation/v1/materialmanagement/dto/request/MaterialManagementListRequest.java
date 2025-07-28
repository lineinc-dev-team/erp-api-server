package com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@ParameterObject
@Schema(description = "자재관리 목록 필터 요청")
public record MaterialManagementListRequest(
        @Schema(description = "현장명", example = "서울 APT 신축공사")
        String siteName,

        @Schema(description = "공정명", example = "기초공사")
        String processName,

        @Schema(description = "품명", example = "철근 D10")
        String materialName,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Schema(description = "납품 시작일", example = "2025-07-01")
        LocalDate deliveryStartDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Schema(description = "납품 종료일", example = "2025-07-31")
        LocalDate deliveryEndDate
) {
}
