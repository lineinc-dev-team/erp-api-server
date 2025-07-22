package com.lineinc.erp.api.server.presentation.v1.site.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@ParameterObject
@Schema(description = "현장 엑셀 다운로드 요청")
public record SiteDownloadRequest(
        @NotBlank
        @Schema(
                description = "허용 필드: id, name, processName, address, type, clientCompanyName, period, processStatuses, createdBy, createdAt, hasFile ,memo",
                example = "id, name, address"
        )
        String fields
) {
    public static final List<String> ALLOWED_FIELDS = List.of(
            "id",
            "name",
            "processName",
            "address",
            "type",
            "clientCompanyName",
            "period",
            "processStatuses",
            "createdBy",
            "createdAt",
            "hasFile",
            "memo"
    );
}
