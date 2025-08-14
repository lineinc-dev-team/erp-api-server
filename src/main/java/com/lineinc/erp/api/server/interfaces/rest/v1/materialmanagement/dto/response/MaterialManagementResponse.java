package com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.site.SiteResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.siteprocess.SiteProcessResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "자재관리 정보 응답")
public record MaterialManagementResponse(
        @Schema(description = "자재관리 ID", example = "1") Long id,

        @Schema(description = "투입 구분", example = "주요자재(구매)") String inputType,

        @Schema(description = "투입 구분 코드", example = "MAJOR_PURCHASE") String inputTypeCode,

        @Schema(description = "투입 구분 상세 설명", example = "외주사 납품") String inputTypeDescription,

        @Schema(description = "납품일자", example = "2025-07-28") OffsetDateTime deliveryDate,

        @Schema(description = "첨부파일 존재 여부", example = "true") Boolean hasFile,

        @Schema(description = "비고", example = "1차 납품 완료") String memo,

        @Schema(description = "현장 요약 정보") SiteResponse.SiteSimpleResponse site,

        @Schema(description = "공정 요약 정보") SiteProcessResponse.SiteProcessSimpleResponse process,

        @Schema(description = "자재업체 요약 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany,

        @Schema(description = "상세 목록") List<MaterialManagementDetailResponse> details) {
    public static MaterialManagementResponse from(MaterialManagement entity) {
        return new MaterialManagementResponse(
                entity.getId(),
                entity.getInputType() != null ? entity.getInputType().getLabel() : null,
                entity.getInputType() != null ? entity.getInputType().name() : null,
                entity.getInputTypeDescription(),
                entity.getDeliveryDate() != null ? entity.getDeliveryDate() : null,
                entity.getFiles() != null && !entity.getFiles().isEmpty(),
                entity.getMemo(),
                SiteResponse.SiteSimpleResponse.from(entity.getSite()),
                SiteProcessResponse.SiteProcessSimpleResponse.from(entity.getSiteProcess()),
                entity.getOutsourcingCompany() != null
                        ? CompanyResponse.CompanySimpleResponse.from(entity.getOutsourcingCompany())
                        : null,
                entity.getDetails().stream()
                        .map(MaterialManagementDetailResponse::from)
                        .collect(Collectors.toList()));
    }
}