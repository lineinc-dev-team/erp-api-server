package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContractOutsourcingContract;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborNameResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractListResponse.ContractSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 직영/용역 외주 응답")
public record DailyReportDirectContractOutsourcingContractResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "공수", example = "8.0") Double workQuantity,
        @Schema(description = "비고", example = "오전 작업") String memo,
        @Schema(description = "인력 정보") LaborNameResponse labor,
        @Schema(description = "업체 정보") CompanySimpleResponse outsourcingCompany,
        @Schema(description = "계약 정보") ContractSimpleResponse outsourcingCompanyContract,
        @Schema(description = "사진 URL", example = "https://example.com/photo.jpg") String fileUrl,
        @Schema(description = "사진 원본 파일명", example = "photo.jpg") String originalFileName,
        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportDirectContractOutsourcingContractResponse from(
            final DailyReportDirectContractOutsourcingContract directContractOutsourcingContract) {
        return new DailyReportDirectContractOutsourcingContractResponse(
                directContractOutsourcingContract.getId(),
                directContractOutsourcingContract.getWorkQuantity(),
                directContractOutsourcingContract.getMemo(),
                directContractOutsourcingContract.getLabor() != null
                        ? LaborNameResponse.from(directContractOutsourcingContract.getLabor())
                        : null,
                directContractOutsourcingContract.getOutsourcingCompany() != null
                        ? CompanySimpleResponse.from(directContractOutsourcingContract.getOutsourcingCompany())
                        : null,
                directContractOutsourcingContract.getOutsourcingCompanyContract() != null
                        ? ContractSimpleResponse.from(directContractOutsourcingContract.getOutsourcingCompanyContract())
                        : null,
                directContractOutsourcingContract.getFileUrl(),
                directContractOutsourcingContract.getOriginalFileName(),
                directContractOutsourcingContract.getCreatedAt(),
                directContractOutsourcingContract.getUpdatedAt());
    }
}
