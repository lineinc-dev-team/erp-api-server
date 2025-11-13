package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingConstruction;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractConstructionResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 외주(공사) 응답")
public record DailyReportOutsourcingConstructionResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "외주업체계약 공사항목 정보") ContractConstructionResponse.ContractConstructionSimpleResponse outsourcingCompanyContractConstruction,
        @Schema(description = "수량", example = "100") Integer quantity,
        @Schema(description = "파일 URL", example = "https://example.com/contract.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "contract.pdf") String originalFileName,
        @Schema(description = "비고", example = "1층 기초공사") String memo,
        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportOutsourcingConstructionResponse from(
            final DailyReportOutsourcingConstruction construction) {
        return new DailyReportOutsourcingConstructionResponse(
                construction.getId(),
                construction.getOutsourcingCompanyContractConstruction() != null
                        ? ContractConstructionResponse.ContractConstructionSimpleResponse
                                .from(construction.getOutsourcingCompanyContractConstruction())
                        : null,
                construction.getQuantity(),
                construction.getFileUrl(),
                construction.getOriginalFileName(),
                construction.getMemo(),
                construction.getCreatedAt(),
                construction.getUpdatedAt());
    }
}
