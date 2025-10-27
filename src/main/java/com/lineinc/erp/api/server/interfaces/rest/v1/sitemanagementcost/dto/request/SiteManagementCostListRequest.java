package com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 현장관리비 목록 조회 요청
 */
@ParameterObject
@Schema(description = "현장관리비 목록 조회 요청")
public record SiteManagementCostListRequest(
        @Schema(description = "시작 년월 (YYYY-MM)", example = "2025-01") String startYearMonth,
        @Schema(description = "종료 년월 (YYYY-MM)", example = "2025-12") String endYearMonth,
        @Schema(description = "현장명", example = "서울 APT") String siteName,
        @Schema(description = "공정명", example = "토공사") String siteProcessName) {
}
