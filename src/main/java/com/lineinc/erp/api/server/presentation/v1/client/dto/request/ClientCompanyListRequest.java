package com.lineinc.erp.api.server.presentation.v1.client.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Schema(description = "발주처 검색 및 페이징 요청")
public record ClientCompanyListRequest(
        @Min(value = 0)
        @Schema(description = "0부터 시작하는 페이지 번호", example = "0")
        int page,

        @Min(value = 1)
        @Max(value = 200)
        @Schema(description = "한 페이지에 포함될 아이템 수", example = "20")
        int size,

        @Schema(description = "정렬 조건", example = "id,asc")
        String sort
) {
}