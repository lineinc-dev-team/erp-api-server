package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import java.util.List;

import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportEvidenceFileType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "출역일보 증빙 파일 수정 요청")
public record DailyReportEvidenceFileUpdateRequest(
        @Schema(description = "수정할 파일 정보 목록") List<@Valid EvidenceFileUpdateInfo> files,
        @Schema(description = "파일 타입", example = "EMPLOYEE") @NotNull DailyReportEvidenceFileType fileType) {
    @Schema(description = "파일 정보 수정 내용")
    public record EvidenceFileUpdateInfo(
            @Schema(description = "ID", example = "1") Long id,
            @Schema(description = "파일명", example = "출역일보_20240115.pdf") @NotEmpty String name,
            @Schema(description = "파일 URL", example = "https://example.com/files/report.pdf") String fileUrl,
            @Schema(description = "원본 파일명", example = "출역일보_20240115.pdf") String originalFileName,
            @Schema(description = "비고", example = "오전 작업 현장") String memo) {
    }
}
