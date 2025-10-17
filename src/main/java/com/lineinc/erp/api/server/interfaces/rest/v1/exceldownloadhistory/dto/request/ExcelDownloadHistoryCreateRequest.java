package com.lineinc.erp.api.server.interfaces.rest.v1.exceldownloadhistory.dto.request;

import com.lineinc.erp.api.server.domain.exceldownloadhistory.enums.ExcelDownloadHistoryType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "엑셀 다운로드 이력 생성 요청")
public record ExcelDownloadHistoryCreateRequest(
        @NotNull @Schema(description = "다운로드 타입", example = "STEEL_MANAGEMENT") ExcelDownloadHistoryType downloadType,
        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/bucket/excel/file.xlsx") String fileUrl) {
}
