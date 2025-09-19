package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.clientcompany.entity.ClientCompanyFile;
import com.lineinc.erp.api.server.domain.clientcompany.enums.ClientCompanyFileType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "발주처 파일 정보 응답")
public record ClientCompanyFileResponse(
        @Schema(description = "파일 ID", example = "1001") Long id,
        @Schema(description = "파일명 (사용자 지정 이름)", example = "사업자등록증") String name,
        @Schema(description = "파일 저장 URL", example = "https://cdn.example.com/files/abc-123.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "biz-license.pdf") String originalFileName,
        @Schema(description = "파일 타입", example = "사업자등록증") String type,
        @Schema(description = "파일 타입 코드", example = "BUSINESS_LICENSE") ClientCompanyFileType typeCode,
        @Schema(description = "비고", example = "2023년 변경된 사업자등록증") String memo,
        @Schema(description = "등록일", example = "2024-01-01T12:34:56") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-01T12:34:56") OffsetDateTime updatedAt) {
    public static ClientCompanyFileResponse from(final ClientCompanyFile file) {
        return new ClientCompanyFileResponse(
                file.getId(),
                file.getName(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getType().getLabel(),
                file.getType(),
                file.getMemo(),
                file.getCreatedAt(),
                file.getUpdatedAt());
    }
}