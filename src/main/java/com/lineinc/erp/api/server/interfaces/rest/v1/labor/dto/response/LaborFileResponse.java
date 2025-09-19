package com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.labor.entity.LaborFile;
import com.lineinc.erp.api.server.domain.labor.enums.LaborFileType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력정보 첨부파일 응답")
public record LaborFileResponse(
        @Schema(description = "파일 ID") Long id,
        @Schema(description = "파일명") String name,
        @Schema(description = "파일 URL") String fileUrl,
        @Schema(description = "원본 파일명") String originalFileName,
        @Schema(description = "파일 타입") String type,
        @Schema(description = "파일 타입 코드") LaborFileType typeCode,
        @Schema(description = "메모") String memo,
        @Schema(description = "등록일") OffsetDateTime createdAt,
        @Schema(description = "수정일") OffsetDateTime updatedAt) {

    public static LaborFileResponse from(LaborFile laborFile) {
        return new LaborFileResponse(
                laborFile.getId(),
                laborFile.getName(),
                laborFile.getFileUrl(),
                laborFile.getOriginalFileName(),
                laborFile.getType() != null ? laborFile.getType().getLabel() : null,
                laborFile.getType(),
                laborFile.getMemo(),
                laborFile.getCreatedAt(),
                laborFile.getUpdatedAt());
    }
}
