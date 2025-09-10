package com.lineinc.erp.api.server.interfaces.rest.v1.file.dto.request;

import com.lineinc.erp.api.server.shared.enums.FileUploadTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Presigned URL 요청")
public record PresignedUrlRequest(
        @Schema(description = "업로드할 파일의 MIME 타입 (이미지, 문서, 오디오, 비디오, 텍스트 파일 등 지원)", example = "image/jpeg") @NotNull String contentType,

        @Schema(description = "파일 업로드 요청이 발생한 비즈니스 도메인", example = "CLIENT_COMPANY") @NotNull FileUploadTarget uploadTarget) {
}
