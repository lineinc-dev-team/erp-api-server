package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request;

import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;

@ParameterObject
@Schema(description = "강재수불부 V2 목록 조회 요청")
public record SteelManagementV2ListRequest(
        @Schema(description = "현장명", example = "서울지사 공사현장") String siteName,
        @Schema(description = "공정명", example = "철근 배근 작업") String siteProcessName,
        @Schema(description = "등록일 시작일", example = "2024-01-01") LocalDate createdStartDate,
        @Schema(description = "등록일 종료일", example = "2024-12-31") LocalDate createdEndDate) {
}
