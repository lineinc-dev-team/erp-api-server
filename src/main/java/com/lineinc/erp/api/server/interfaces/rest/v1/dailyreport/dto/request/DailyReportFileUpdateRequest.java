package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "출역일보 파일 수정 요청")
public record DailyReportFileUpdateRequest(
        @Schema(description = "수정할 파일 정보 목록") List<@Valid FileUpdateInfo> files) {

    @Schema(description = "파일 정보 수정 내용")
    public record FileUpdateInfo(
            @Schema(description = "ID", example = "1") @NotNull Long id,
            @Schema(description = "파일 URL", example = "https://example.com/files/report.pdf") String fileUrl,
            @Schema(description = "원본 파일명", example = "출역일보_20240115.pdf") String originalFileName,
            @Schema(description = "파일 설명", example = "현장 사진") @NotBlank String description,
            @Schema(description = "비고", example = "오전 작업 현장") String memo) {
    }
}
