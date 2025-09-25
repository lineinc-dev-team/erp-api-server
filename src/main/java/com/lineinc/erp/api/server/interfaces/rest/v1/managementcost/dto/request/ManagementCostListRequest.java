package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType;

import io.swagger.v3.oas.annotations.media.Schema;

@ParameterObject
@Schema(description = "관리비 검색 요청")
public record ManagementCostListRequest(
        @Schema(description = "현장명", example = "서울 APT 신축공사") String siteName,
        @Schema(description = "공정명", example = "기초 공사") String processName,
        @Schema(description = "외주 업체명", example = "삼성 ENG") String outsourcingCompanyName,
        @Schema(description = "항목 타입", example = "UTILITY_ELECTRICITY") ManagementCostItemType itemType,
        @Schema(description = "항목 타입 설명", example = "관리비(가스/전기/수도)") String itemTypeDescription,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Schema(description = "결제 시작일", example = "2024-01-01") LocalDate paymentStartDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Schema(description = "결제 종료일", example = "2025-12-31") LocalDate paymentEndDate) {
}