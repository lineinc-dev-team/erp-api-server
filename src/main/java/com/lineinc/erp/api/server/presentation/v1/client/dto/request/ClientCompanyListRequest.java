package com.lineinc.erp.api.server.presentation.v1.client.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Schema(description = "발주처 검색 및 페이징 요청")
public record ClientCompanyListRequest(
        @Schema(description = "발주처명", example = "삼성")
        String name,

        @Schema(description = "사업자등록번호", example = "123-45-67890")
        String businessNumber
) {
}