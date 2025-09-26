package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContract;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborNameResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 직영/계약직 응답")
public record DailyReportDirectContractResponse(
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

    public static DailyReportDirectContractResponse from(final DailyReportDirectContract directContract) {
        return new DailyReportDirectContractResponse(
                directContract.getId(),
                directContract.getPosition(),
                directContract.getWorkContent(),
                directContract.getUnitPrice(),
                directContract.getWorkQuantity(),
                directContract.getMemo(),
                directContract.getLabor() != null ? LaborNameResponse.from(directContract.getLabor()) : null,
                directContract.getOutsourcingCompany() != null
                        ? CompanySimpleResponse.from(directContract.getOutsourcingCompany())
                        : null,
                directContract.getFileUrl(),
                directContract.getOriginalFileName(),
                directContract.getCreatedAt(),
                directContract.getUpdatedAt());
    }
}
