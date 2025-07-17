package com.lineinc.erp.api.server.presentation.v1.file.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "S3 Presigned URL 응답")
public record PresignedUrlResponse(

        @Schema(description = "S3 presigned URL (업로드용)", example = "https://lineinc-erp-dev-files.s3.ap-northeast-2.amazonaws.com/temp/uuid?...")
        String s3UploadUrl,

        @Schema(description = "CDN을 통한 접근용 URL", example = "https://dev-cdn.dooson.it/temp/uuid")
        String cdnAccessUrl
) {
    public static PresignedUrlResponse of(String uploadUrl, String cdnUrl) {
        return new PresignedUrlResponse(uploadUrl, cdnUrl);
    }
}