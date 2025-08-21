package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import com.lineinc.erp.api.server.domain.managementcost.enums.ItemType;

import java.time.LocalDate;

@ParameterObject
@Schema(description = "관리비 검색 요청")
public record ManagementCostListRequest(
        @Schema(description = "현장명", example = "서울 APT 신축공사")
        String name,

        @Schema(description = "공정명", example = "기초 공사")
        String processName,

        @Schema(description = "항목 타입", example = "MAINTENANCE")
        ItemType itemType,

        @Schema(description = "항목 설명", example = "6월 전기요금")
        String itemDescription,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Schema(description = "결제 시작일", example = "2024-01-01")
        LocalDate paymentStartDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Schema(description = "결제 종료일", example = "2025-12-31")
        LocalDate paymentEndDate
) {
}