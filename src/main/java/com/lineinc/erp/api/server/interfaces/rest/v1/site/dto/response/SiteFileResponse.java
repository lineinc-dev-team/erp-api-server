package com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.lineinc.erp.api.server.domain.site.entity.SiteFile;
import com.lineinc.erp.api.server.domain.site.enums.SiteFileType;

@Schema(description = "현장 파일 정보 응답")
public record SiteFileResponse(

        @Schema(description = "파일 ID", example = "1") Long id,

        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/your-bucket/file.pdf") String fileUrl,

        @Schema(description = "원본 파일명", example = "plan.pdf") String originalFileName,

        @Schema(description = "파일 메모", example = "최종 제출본") String memo,

        @Schema(description = "파일 유형", example = "계약서") String type,

        @Schema(description = "파일 유형 코드", example = "CONTRACT") SiteFileType typeCode

) {
    public static SiteFileResponse from(SiteFile file) {
        return new SiteFileResponse(
                file.getId(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getMemo(),
                file.getType().getLabel(),
                file.getType());
    }
}
