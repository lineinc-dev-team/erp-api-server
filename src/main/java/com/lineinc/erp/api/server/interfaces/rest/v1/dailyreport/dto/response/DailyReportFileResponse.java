package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 파일 응답")
public record DailyReportFileResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "파일 URL", example = "https://example.com/files/report.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "출역일보_20240115.pdf") String originalFileName,
        @Schema(description = "파일 설명", example = "현장 사진") String description,
        @Schema(description = "비고", example = "오전 작업 현장") String memo,
        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportFileResponse from(final DailyReportFile file) {
        return new DailyReportFileResponse(
                file.getId(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getDescription(),
                file.getMemo(),
                file.getCreatedAt(),
                file.getUpdatedAt());
    }
}
