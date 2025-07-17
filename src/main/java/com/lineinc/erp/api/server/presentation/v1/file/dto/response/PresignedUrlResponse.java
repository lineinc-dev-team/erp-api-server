package com.lineinc.erp.api.server.presentation.v1.file.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "S3 Presigned URL 응답")
public record PresignedUrlResponse(

        @Schema(description = "S3 presigned URL (업로드용)", example = "https://lineinc-erp-dev-files.s3.ap-northeast-2.amazonaws.com/temp/uuid?...")
        String s3UploadUrl,

        @Schema(description = "CDN을 통한 접근용 URL", example = "https://dev-cdn.dooson.it/temp/uuid")
        String cdnAccessUrl,

        @Schema(description = "파일 경로 (S3 object key)", example = "temp/uuid")
        String key

) {
    public PresignedUrlResponse(Map<String, Object> map) {
        this(
                (String) map.get("uploadUrl"),
                (String) map.get("url"),
                (String) map.get("key")
        );
    }
}