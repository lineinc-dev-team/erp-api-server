package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingCompany;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 외주업체 응답 (3depth 구조)")
public record DailyReportOutsourcingCompanyResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "업체 정보") CompanySimpleResponse outsourcingCompany,
        @Schema(description = "공사 그룹 목록") List<DailyReportOutsourcingConstructionGroupResponse> groups,
        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportOutsourcingCompanyResponse from(
            final DailyReportOutsourcingCompany outsourcingCompany) {
        return new DailyReportOutsourcingCompanyResponse(
                outsourcingCompany.getId(),
                outsourcingCompany.getOutsourcingCompany() != null
                        ? CompanySimpleResponse.from(outsourcingCompany.getOutsourcingCompany())
                        : null,
                outsourcingCompany.getConstructionGroups() != null
                        ? outsourcingCompany.getConstructionGroups().stream()
                                .map(DailyReportOutsourcingConstructionGroupResponse::from)
                                .toList()
                        : List.of(),
                outsourcingCompany.getCreatedAt(),
                outsourcingCompany.getUpdatedAt());
    }
}
