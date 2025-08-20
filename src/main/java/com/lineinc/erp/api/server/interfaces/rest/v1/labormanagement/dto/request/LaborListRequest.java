package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Schema(description = "인력정보 목록 조회 요청")
public record LaborListRequest(
        @Schema(description = "노무 구분") String type,
        @Schema(description = "이름") String name,
        @Schema(description = "주민등록번호") String residentNumber,
        @Schema(description = "소속업체 ID") Long outsourcingCompanyId,
        @Schema(description = "개인 휴대폰") String phoneNumber) {
}
