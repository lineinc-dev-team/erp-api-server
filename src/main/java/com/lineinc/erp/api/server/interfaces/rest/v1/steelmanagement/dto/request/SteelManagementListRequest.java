package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request;

import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;

import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType;

import io.swagger.v3.oas.annotations.media.Schema;

@ParameterObject
@Schema(description = "강재 관리 목록 조회 요청")
public record SteelManagementListRequest(
        @Schema(description = "현장명", example = "서울지사 공사현장") String siteName,

        @Schema(description = "공정명", example = "철근 배근 작업") String processName,

        @Schema(description = "외주업체명", example = "철근공사업체") String outsourcingCompanyName,

        @Schema(description = "품명", example = "철근") String itemName,

        @Schema(description = "기간 시작일", example = "2024-07-01") LocalDate startDate,

        @Schema(description = "기간 종료일", example = "2024-07-31") LocalDate endDate,

        @Schema(description = "강재 수불 구분", example = "ORDER") SteelManagementType type) {
}
