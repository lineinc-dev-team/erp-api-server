package com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@ParameterObject
@Schema(description = "현장 엑셀 다운로드 요청")
public record SiteDownloadRequest(
        @NotBlank @Schema(description = "다운로드할 필드들을 쉼표로 구분", example = EXAMPLE_FIELDS) String fields) {
    private static final String EXAMPLE_FIELDS = "id,name,processName,address,type,clientCompanyName,period,processStatuses,createdBy,createdAt,hasFile,memo,contractAmount,managerName";
    public static final List<String> ALLOWED_FIELDS = List.of(EXAMPLE_FIELDS.split(","));
}
