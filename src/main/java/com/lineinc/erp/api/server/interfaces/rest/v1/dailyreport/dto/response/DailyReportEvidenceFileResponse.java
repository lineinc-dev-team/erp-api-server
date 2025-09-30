package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportEvidenceFile;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportEvidenceFileType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 증빙 파일 응답")
public record DailyReportEvidenceFileResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "파일 타입", example = "EMPLOYEE") DailyReportEvidenceFileType fileType,
        @Schema(description = "파일명", example = "출역일보_20240115.pdf") String name,
        @Schema(description = "파일 URL", example = "https://example.com/files/report.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "출역일보_20240115.pdf") String originalFileName,
        @Schema(description = "비고", example = "오전 작업 현장") String memo) {

    public static DailyReportEvidenceFileResponse from(final DailyReportEvidenceFile evidenceFile) {
        return new DailyReportEvidenceFileResponse(
                evidenceFile.getId(),
                evidenceFile.getFileType(),
                evidenceFile.getName(),
                evidenceFile.getFileUrl(),
                evidenceFile.getOriginalFileName(),
                evidenceFile.getMemo());
    }
}
