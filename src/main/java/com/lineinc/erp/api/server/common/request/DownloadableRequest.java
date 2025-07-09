package com.lineinc.erp.api.server.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@ParameterObject
@Schema(description = "엑셀 다운로드 시 포함할 필드 요청")
public record DownloadableRequest(
        @NotBlank
        @Schema(description = "쉼표로 구분된 다운로드 필드 목록 (예: id,name,businessNumber)")
        String fields
) {
    public List<String> parsedFields() {
        return fields == null || fields.isBlank()
                ? List.of()
                : List.of(fields.split("\\s*,\\s*")); // 공백 제거
    }

    public List<String> validatedFields(List<String> allowedFields) {
        List<String> parsed = parsedFields();
        for (String field : parsed) {
            if (!allowedFields.contains(field)) {
                throw new IllegalArgumentException(field);
            }
        }
        return parsed;
    }
}