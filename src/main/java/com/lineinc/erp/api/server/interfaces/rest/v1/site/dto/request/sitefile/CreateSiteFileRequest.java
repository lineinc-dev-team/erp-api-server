package com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.sitefile;

import com.lineinc.erp.api.server.domain.site.enums.SiteFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "현장 파일 등록 요청")
public record CreateSiteFileRequest(
        @NotBlank
        @Schema(description = "문서명", example = "현장도면 v1")
        String name,

        @NotBlank
        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/bucket/folder/file.pdf")
        String fileUrl,

        @Schema(description = "원본 파일명", example = "file.pdf")
        String originalFileName,

        @Schema(description = "비고 또는 설명", example = "1차 계약서 스캔본")
        String memo,

        @NotNull
        @Schema(description = "파일 유형", example = "CONTRACT")
        SiteFileType type
) {
}
