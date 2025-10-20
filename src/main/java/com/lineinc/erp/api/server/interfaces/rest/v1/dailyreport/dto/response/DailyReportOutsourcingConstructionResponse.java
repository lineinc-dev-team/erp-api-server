package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingConstruction;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractConstructionResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 외주(공사) 응답")
public record DailyReportOutsourcingConstructionResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "단위", example = "m²") String unit,
        @Schema(description = "수량", example = "100") Integer quantity,
        @Schema(description = "비고", example = "1층 기초공사") String memo,
        @Schema(description = "외주업체 정보") CompanySimpleResponse outsourcingCompany,
        @Schema(description = "외주업체계약 공사항목 정보") ContractConstructionResponse outsourcingCompanyContractConstruction,
        @Schema(description = "계약서 파일 URL", example = "https://example.com/contract.pdf") String contractFileUrl,
        @Schema(description = "계약서 원본 파일명", example = "contract.pdf") String contractOriginalFileName,
        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportOutsourcingConstructionResponse from(
            final DailyReportOutsourcingConstruction construction) {
        return new DailyReportOutsourcingConstructionResponse(
                construction.getId(),
                construction.getUnit(),
                construction.getQuantity(),
                construction.getMemo(),
                construction.getOutsourcingCompany() != null
                        ? CompanySimpleResponse.from(construction.getOutsourcingCompany())
                        : null,
                construction.getOutsourcingCompanyContractConstruction() != null
                        ? ContractConstructionResponse
                                .from(construction.getOutsourcingCompanyContractConstruction())
                        : null,
                construction.getContractFileUrl(),
                construction.getContractOriginalFileName(),
                construction.getCreatedAt(),
                construction.getUpdatedAt());
    }
}
