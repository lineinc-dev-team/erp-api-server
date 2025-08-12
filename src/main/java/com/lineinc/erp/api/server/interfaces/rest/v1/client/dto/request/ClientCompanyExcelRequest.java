package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;


@ParameterObject
@Schema(description = "발주처 엑셀 다운로드 요청")
public record ClientCompanyExcelRequest(
        @Schema(description = "발주처 검색 요청")
        ClientCompanyListRequest clientCompanyListRequest
) {

}