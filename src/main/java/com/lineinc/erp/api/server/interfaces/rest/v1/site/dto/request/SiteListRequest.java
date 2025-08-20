package com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request;

import java.time.LocalDate;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus;
import com.lineinc.erp.api.server.domain.site.enums.SiteType;

import io.swagger.v3.oas.annotations.media.Schema;

@ParameterObject
@Schema(description = "현장 검색 요청")
public record SiteListRequest(
        @Schema(description = "현장명", example = "서울 APT 신축공사") String name,

        @Schema(description = "현장 유형", example = "CONSTRUCTION") SiteType type,

        @Schema(description = "공정명", example = "기초 공사") String processName,

        @Schema(description = "시 (City)", example = "서울시") String city,

        @Schema(description = "도/구 (District)", example = "강남구") String district,

        @Schema(description = "공정 진행 상태 목록", example = "[\"GASOLINE\", \"UREA\"]") List<SiteProcessStatus> processStatuses,

        @Schema(description = "발주처명", example = "삼성건설") String clientCompanyName,

        @Schema(description = "등록자명", example = "홍길동") String createdBy,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Schema(description = "사업 시작일", example = "2024-01-01") LocalDate startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Schema(description = "사업 종료일", example = "2025-12-31") LocalDate endDate,

        @Schema(description = "등록일 시작일", example = "2024-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdStartDate,

        @Schema(description = "등록일 종료일", example = "2024-12-31") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdEndDate,

        @Schema(description = "공정 소장명", example = "홍길동") String managerName) {
}