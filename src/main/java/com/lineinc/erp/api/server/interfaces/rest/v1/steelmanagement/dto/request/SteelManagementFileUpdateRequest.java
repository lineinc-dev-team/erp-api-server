package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재 관리 파일 수정 요청")
public record SteelManagementFileUpdateRequest(
        @Schema(description = "강재 관리 파일 상세 ID", example = "1")
        Long id,

        @Schema(description = "파일 이름", example = "강재_입고내역.pdf")
        String name,

        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/bucket/folder/file.pdf")
        String fileUrl,

        @Schema(description = "원본 파일명", example = "steel_report.pdf")
        String originalFileName,

        @Schema(description = "비고", example = "6월 강재 입고자료")
        String memo
) {
}
