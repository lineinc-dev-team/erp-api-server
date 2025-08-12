package com.lineinc.erp.api.server.interfaces.rest.v1.file.dto.request;

import com.lineinc.erp.api.server.shared.enums.FileUploadTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Presigned URL 요청")
public record PresignedUrlRequest(
        @Schema(
                description = "업로드할 파일의 MIME 타입 허용 범위 (image/jpeg, image/png, application/pdf, application/msword, application/vnd.openxmlformats-officedocument.wordprocessingml.document, application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/x-hwp, application/zip)",
                example = "image/jpeg"
        )
        @NotNull
        String contentType,

        @Schema(description = "파일 업로드 요청이 발생한 비즈니스 도메인", example = "CLIENT_COMPANY")
        @NotNull
        FileUploadTarget uploadTarget
) {
}
