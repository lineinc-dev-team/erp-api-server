package com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request;

import com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus;
import com.lineinc.erp.api.server.domain.site.enums.SiteType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@ParameterObject
@Schema(description = "관리비 검색 요청")
public record ManagementCostListRequest(
        @Schema(description = "현장명", example = "서울 APT 신축공사")
        String name,

        @Schema(description = "공정명", example = "기초 공사")
        String processName,


        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Schema(description = "사업 시작일", example = "2024-01-01")
        LocalDate startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Schema(description = "사업 종료일", example = "2025-12-31")
        LocalDate endDate,

        @Schema(description = "등록일 시작일", example = "2024-01-01")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createdStartDate,

        @Schema(description = "등록일 종료일", example = "2024-12-31")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createdEndDate

) {
}