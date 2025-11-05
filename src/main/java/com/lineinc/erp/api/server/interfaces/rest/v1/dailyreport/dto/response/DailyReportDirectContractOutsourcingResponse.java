package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContractOutsourcing;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborNameResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 직영/용역 용역 응답")
public record DailyReportDirectContractOutsourcingResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "직종", example = "토목공") String position,
        @Schema(description = "작업내용", example = "기초 콘크리트 타설") String workContent,
        @Schema(description = "단가", example = "50000") Long unitPrice,
        @Schema(description = "공수", example = "8.0") Double workQuantity,
        @Schema(description = "비고", example = "오전 작업") String memo,
        @Schema(description = "인력 정보") LaborNameResponse labor,
        @Schema(description = "업체 정보") CompanySimpleResponse outsourcingCompany,
        @Schema(description = "사진 URL", example = "https://example.com/photo.jpg") String fileUrl,
        @Schema(description = "사진 원본 파일명", example = "photo.jpg") String originalFileName,
        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportDirectContractOutsourcingResponse from(
            final DailyReportDirectContractOutsourcing directContractOutsourcing) {
        return new DailyReportDirectContractOutsourcingResponse(
                directContractOutsourcing.getId(),
                directContractOutsourcing.getPosition(),
                directContractOutsourcing.getWorkContent(),
                directContractOutsourcing.getUnitPrice(),
                directContractOutsourcing.getWorkQuantity(),
                directContractOutsourcing.getMemo(),
                directContractOutsourcing.getLabor() != null
                        ? LaborNameResponse.from(directContractOutsourcing.getLabor())
                        : null,
                directContractOutsourcing.getOutsourcingCompany() != null
                        ? CompanySimpleResponse.from(directContractOutsourcing.getOutsourcingCompany())
                        : null,
                directContractOutsourcing.getFileUrl(),
                directContractOutsourcing.getOriginalFileName(),
                directContractOutsourcing.getCreatedAt(),
                directContractOutsourcing.getUpdatedAt());
    }
}
