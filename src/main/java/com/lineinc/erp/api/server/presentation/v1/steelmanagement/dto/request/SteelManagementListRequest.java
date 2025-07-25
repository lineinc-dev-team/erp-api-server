package com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Schema(description = "강재 관리 목록 조회 요청")
public record SteelManagementListRequest(
        @Schema(description = "현장명", example = "서울지사 공사현장")
        String siteName,

        @Schema(description = "공정명", example = "철근 배근 작업")
        String processName,

        @Schema(description = "품명", example = "철근")
        String itemName,

        @Schema(description = "구매 시작일자", example = "2024-07-01")
        LocalDate paymentStartDate,

        @Schema(description = "구매 종료일자", example = "2024-07-31")
        LocalDate paymentEndDate,

        @Schema(description = "강재 수불 구분", example = "ORDER")
        SteelManagementType type
) {
}
