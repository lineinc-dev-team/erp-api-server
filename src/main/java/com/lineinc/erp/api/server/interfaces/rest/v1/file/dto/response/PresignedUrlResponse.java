package com.lineinc.erp.api.server.interfaces.rest.v1.file.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파일 업로드용 Presigned URL 응답")
public record PresignedUrlResponse(

        @Schema(description = "파일 업로드용 Presigned URL", example = "https://lineinc-erp-dev-files.s3.ap-northeast-2.amazonaws.com/temp/uuid?...")
        String uploadUrl,

        @Schema(description = "클라이언트 접근용 공개 URL", example = "https://dev-cdn.dooson.it/temp/uuid")
        String publicUrl
) {
    public static PresignedUrlResponse of(String uploadUrl, String publicUrl) {
        return new PresignedUrlResponse(uploadUrl, publicUrl);
    }
}