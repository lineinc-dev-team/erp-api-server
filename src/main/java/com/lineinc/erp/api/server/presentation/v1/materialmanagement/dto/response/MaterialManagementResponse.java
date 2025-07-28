package com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.response;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.SiteResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "자재관리 정보 응답")
public record MaterialManagementResponse(
        @Schema(description = "자재관리 ID", example = "1")
        Long id,

        @Schema(description = "투입 구분 (ENUM 이름)", example = "PURCHASE")
        String inputType,

        @Schema(description = "투입 구분 상세 설명", example = "외주사 납품")
        String inputTypeDescription,

        @Schema(description = "납품일자", example = "2025-07-28")
        OffsetDateTime deliveryDate,

        @Schema(description = "비고", example = "1차 납품 완료")
        String memo,

        @Schema(description = "현장 요약 정보")
        SiteResponse.SiteSimpleResponse site,

        @Schema(description = "공정 요약 정보")
        SiteProcessResponse.SiteProcessSimpleResponse process,

        @Schema(description = "상세 목록")
        List<MaterialManagementDetailResponse> details,

        @Schema(description = "파일 목록")
        List<MaterialManagementFileResponse> files
) {
    public static MaterialManagementResponse from(MaterialManagement entity) {
        return new MaterialManagementResponse(
                entity.getId(),
                entity.getInputType() != null ? entity.getInputType().name() : null,
                entity.getInputTypeDescription(),
                entity.getDeliveryDate() != null ? entity.getDeliveryDate() : null,
                entity.getMemo(),
                SiteResponse.SiteSimpleResponse.from(entity.getSite()),
                SiteProcessResponse.SiteProcessSimpleResponse.from(entity.getSiteProcess()),
                entity.getDetails().stream()
                        .map(MaterialManagementDetailResponse::from)
                        .collect(Collectors.toList()),
                entity.getFiles().stream()
                        .map(MaterialManagementFileResponse::from)
                        .collect(Collectors.toList())
        );
    }
}