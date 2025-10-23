package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingConstructionGroup;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractConstructionGroupResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 외주업체 공사 그룹 응답")
public record DailyReportOutsourcingConstructionGroupResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "외주업체계약 공사항목 그룹 정보") ContractConstructionGroupResponse.ContractConstructionGroupSimpleResponseForDailyReport outsourcingCompanyContractConstructionGroup,
        @Schema(description = "공사항목 목록") List<DailyReportOutsourcingConstructionResponse> items,
        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportOutsourcingConstructionGroupResponse from(
            final DailyReportOutsourcingConstructionGroup group) {
        return new DailyReportOutsourcingConstructionGroupResponse(
                group.getId(),
                group.getOutsourcingCompanyContractConstructionGroup() != null
                        ? ContractConstructionGroupResponse.ContractConstructionGroupSimpleResponseForDailyReport
                                .from(group.getOutsourcingCompanyContractConstructionGroup())
                        : null,
                group.getConstructions() != null
                        ? group.getConstructions().stream()
                                .map(DailyReportOutsourcingConstructionResponse::from)
                                .toList()
                        : List.of(),
                group.getCreatedAt(),
                group.getUpdatedAt());
    }
}
