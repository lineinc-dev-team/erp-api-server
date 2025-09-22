package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재 관리 목록 응답")
public record SteelManagementResponse(
        @Schema(description = "강재 관리 ID", example = "1") Long id,

        @Schema(description = "용도", example = "철근 콘크리트 타설용") String usage,

        @Schema(description = "강재 수불 유형", example = "PURCHASE") String type,

        @Schema(description = "강재 수불 유형 코드", example = "PURCHASE") String typeCode,

        @Schema(description = "기간 시작일", example = "2024-07-01T00:00:00+09:00") OffsetDateTime startDate,

        @Schema(description = "기간 종료일", example = "2024-07-31T00:00:00+09:00") OffsetDateTime endDate,

        @Schema(description = "주문일", example = "2024-07-15T00:00:00+09:00") OffsetDateTime orderDate,

        @Schema(description = "승인일", example = "2024-07-20T00:00:00+09:00") OffsetDateTime approvalDate,

        @Schema(description = "반출일", example = "2024-07-25T00:00:00+09:00") OffsetDateTime releaseDate,

        @Schema(description = "첨부파일 존재 여부", example = "true") boolean hasFile,

        @Schema(description = "비고", example = "7월 강재 입출고 내역") String memo,

        @Schema(description = "현장 요약 정보") SiteResponse.SiteSimpleResponse site,

        @Schema(description = "공정 요약 정보") SiteProcessResponse.SiteProcessSimpleResponse process,

        @Schema(description = "외주업체 요약 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany,

        @Schema(description = "총금액", example = "1000000") Long totalAmount

) {
    public static SteelManagementResponse from(final SteelManagement entity) {
        // 총금액 계산 (상세 품목의 공급가 합계)
        final Long totalAmount = entity.getDetails().stream()
                .mapToLong(detail -> detail.getSupplyPrice() != null ? detail.getSupplyPrice() : 0L)
                .sum();

        return new SteelManagementResponse(
                entity.getId(),
                entity.getUsage(),
                entity.getType() != null ? entity.getType().getLabel() : null,
                entity.getType() != null ? entity.getType().name() : null,
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getOrderDate(),
                entity.getApprovalDate(),
                entity.getReleaseDate(),
                entity.getFiles() != null && entity.getFiles().stream()
                        .anyMatch(file -> file.getFileUrl() != null && !file.getFileUrl().trim().isEmpty()),
                entity.getMemo(),
                SiteResponse.SiteSimpleResponse.from(entity.getSite()),
                SiteProcessResponse.SiteProcessSimpleResponse.from(entity.getSiteProcess()),
                entity.getOutsourcingCompany() != null
                        ? CompanyResponse.CompanySimpleResponse.from(entity.getOutsourcingCompany())
                        : null,
                totalAmount);
    }
}
