package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재 관리 파일 정보 응답")
public record SteelManagementFileResponse(
        @Schema(description = "파일 ID", example = "1") Long id,

        @Schema(description = "문서명", example = "철근자재_내역서.pdf") String name,

        @Schema(description = "파일 URL", example = "https://s3.bucket.com/path/to/file.pdf") String fileUrl,

        @Schema(description = "원본 파일명", example = "내역서.pdf") String originalFileName,

        @Schema(description = "비고", example = "철근 수불부 첨부파일") String memo) {

    public static SteelManagementFileResponse from(SteelManagementFile file) {
        return new SteelManagementFileResponse(
                file.getId(),
                file.getName(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getMemo());
    }
}
