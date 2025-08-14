package com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementInputType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "자재관리 수정 요청")
public record MaterialManagementUpdateRequest(

        @Schema(description = "현장 ID", example = "1") Long siteId,

        @Schema(description = "공정 ID", example = "1") Long siteProcessId,

        @Schema(description = "외주업체 ID", example = "1") Long outsourcingCompanyId,

        @Schema(description = "투입 구분", example = "MAJOR_PURCHASE") MaterialManagementInputType inputType,

        @Schema(description = "투입 구분 상세", example = "외주사 납품") String inputTypeDescription,

        @Schema(description = "납품일자", example = "2024-07-28") LocalDate deliveryDate,

        @Schema(description = "비고", example = "1차 자재 납품 완료") String memo,

        @Schema(description = "자재 상세 목록") List<MaterialManagementDetailUpdateRequest> details,

        @Schema(description = "자재 파일 목록") List<MaterialManagementFileUpdateRequest> files) {
}
