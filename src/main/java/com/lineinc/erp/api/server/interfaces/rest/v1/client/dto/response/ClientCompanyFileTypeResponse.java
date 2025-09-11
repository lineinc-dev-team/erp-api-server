package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response;

import com.lineinc.erp.api.server.domain.client.enums.ClientCompanyFileType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 발주처 파일 타입 응답
 */
@Schema(description = "발주처 파일 타입 응답")
public record ClientCompanyFileTypeResponse(
        @Schema(description = "파일 타입 코드", example = "BUSINESS_LICENSE") String code,

        @Schema(description = "파일 타입 표시명", example = "사업자등록증") String name) {
    public static ClientCompanyFileTypeResponse from(ClientCompanyFileType fileType) {
        return new ClientCompanyFileTypeResponse(fileType.name(), fileType.getLabel());
    }
}
