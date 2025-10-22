package com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response;

import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;
import com.lineinc.erp.api.server.shared.util.PrivacyMaskingUtils;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력명 응답")
public record LaborNameResponse(
        @Schema(description = "인력 ID", example = "1") Long id,
        @Schema(description = "인력명", example = "홍길동") String name,
        @Schema(description = "기준일당", example = "100000") Long dailyWage,
        @Schema(description = "이전단가", example = "95000") Long previousDailyWage,
        @Schema(description = "노무인력 유형", example = "정직원") String type,
        @Schema(description = "임시 인력 여부", example = "false") Boolean isTemporary,
        @Schema(description = "퇴직금 발생 여부", example = "false") Boolean isSeverancePayEligible,
        @Schema(description = "노무인력 유형 코드", example = "REGULAR_EMPLOYEE") LaborType typeCode,
        @Schema(description = "소속업체 정보") CompanySimpleResponse outsourcingCompany,
        @Schema(description = "주민등록번호") String residentNumber,
        @Schema(description = "휴대폰 번호") String phoneNumber,
        @Schema(description = "본사 여부", example = "false") Boolean isHeadOffice) {

    public static LaborNameResponse from(final Labor labor) {
        return new LaborNameResponse(
                labor.getId(),
                labor.getName(),
                labor.getDailyWage(),
                labor.getPreviousDailyWage(),
                labor.getType() != null ? labor.getType().getLabel() : null,
                labor.getIsTemporary(),
                labor.getIsSeverancePayEligible(),
                labor.getType(),
                labor.getOutsourcingCompany() != null ? CompanySimpleResponse.from(labor.getOutsourcingCompany())
                        : null,
                PrivacyMaskingUtils.maskResidentNumber(labor.getResidentNumber()),
                labor.getPhoneNumber(),
                labor.getIsHeadOffice());
    }
}
