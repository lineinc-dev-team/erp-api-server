package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContractFile;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportEmployeeFile;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportFuelFile;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingEquipmentFile;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 문서 응답")
public record DailyReportDocumentResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "문서명", example = "출역일보_20240115.pdf") String name,
        @Schema(description = "파일 URL", example = "https://example.com/files/report.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "출역일보_20240115.pdf") String originalFileName,
        @Schema(description = "비고", example = "오전 작업 현장") String memo,
        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportDocumentResponse from(final DailyReportDirectContractFile file) {
        return new DailyReportDocumentResponse(
                file.getId(),
                file.getName(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getMemo(),
                file.getCreatedAt(),
                file.getUpdatedAt());
    }

    public static DailyReportDocumentResponse from(final DailyReportEmployeeFile file) {
        return new DailyReportDocumentResponse(
                file.getId(),
                file.getName(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getMemo(),
                file.getCreatedAt(),
                file.getUpdatedAt());
    }

    public static DailyReportDocumentResponse from(final DailyReportOutsourcingFile file) {
        return new DailyReportDocumentResponse(
                file.getId(),
                file.getName(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getMemo(),
                file.getCreatedAt(),
                file.getUpdatedAt());
    }

    public static DailyReportDocumentResponse from(final DailyReportOutsourcingEquipmentFile file) {
        return new DailyReportDocumentResponse(
                file.getId(),
                file.getName(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getMemo(),
                file.getCreatedAt(),
                file.getUpdatedAt());
    }

    public static DailyReportDocumentResponse from(final DailyReportFuelFile file) {
        return new DailyReportDocumentResponse(
                file.getId(),
                file.getName(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getMemo(),
                file.getCreatedAt(),
                file.getUpdatedAt());
    }
}
