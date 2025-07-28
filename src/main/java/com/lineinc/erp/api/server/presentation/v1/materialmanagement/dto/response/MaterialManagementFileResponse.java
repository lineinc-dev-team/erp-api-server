package com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.response;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementFile;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "자재관리 파일 정보 응답")
public record MaterialManagementFileResponse(
        @Schema(description = "파일 ID", example = "1")
        Long id,

        @Schema(description = "문서명 (사용자가 지정하는 파일 이름)", example = "견적서.pdf")
        String name,

        @Schema(description = "파일 URL (S3 또는 외부 저장소)", example = "https://bucket.s3.amazonaws.com/파일경로/견적서.pdf")
        String fileUrl,

        @Schema(description = "원본 파일명", example = "quotation_original.pdf")
        String originalFileName,

        @Schema(description = "파일에 대한 비고 또는 설명", example = "첫 납품 관련 견적서 파일")
        String memo
) {
    public static MaterialManagementFileResponse from(MaterialManagementFile entity) {
        return new MaterialManagementFileResponse(
                entity.getId(),
                entity.getName(),
                entity.getFileUrl(),
                entity.getOriginalFileName(),
                entity.getMemo()
        );
    }
}