package com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;

import com.lineinc.erp.api.server.domain.labor.enums.LaborType;

@ParameterObject
@Schema(description = "인력정보 목록 조회 요청")
public record LaborListRequest(
        @Schema(description = "구분", example = "ETC") LaborType type,
        @Schema(description = "구분 설명", example = "기술공") String typeDescription,
        @Schema(description = "이름", example = "홍길동") String name,
        @Schema(description = "주민등록번호", example = "860101-1234567") String residentNumber,
        @Schema(description = "소속업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "개인 휴대폰", example = "010-1234-5678") String phoneNumber,
        @Schema(description = "본사직원 여부", example = "false") Boolean isHeadOffice) {
}
