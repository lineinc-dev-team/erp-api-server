package com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.labor.enums.WorkType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;
import com.lineinc.erp.api.server.shared.util.PrivacyMaskingUtils;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력정보 목록 응답")
public record LaborListResponse(
        @Schema(description = "인력 ID") Long id,
        @Schema(description = "노무 구분") String type,
        @Schema(description = "노무 구분 코드") LaborType typeCode,
        @Schema(description = "구분 설명") String typeDescription,
        @Schema(description = "이름") String name,
        @Schema(description = "공종") String workType,
        @Schema(description = "공종 코드") WorkType workTypeCode,
        @Schema(description = "공종 설명") String workTypeDescription,
        @Schema(description = "본사 인력 여부") Boolean isHeadOffice,
        @Schema(description = "주작업") String mainWork,
        @Schema(description = "기준일당") Long dailyWage,
        @Schema(description = "은행명") String bankName,
        @Schema(description = "계좌번호") String accountNumber,
        @Schema(description = "예금주") String accountHolder,
        @Schema(description = "입사일") OffsetDateTime hireDate,
        @Schema(description = "퇴사일") OffsetDateTime resignationDate,
        @Schema(description = "소속업체 정보") CompanySimpleResponse outsourcingCompany,
        @Schema(description = "휴대폰 번호") String phoneNumber,
        @Schema(description = "주민등록번호") String residentNumber,
        @Schema(description = "임시 인력 여부", example = "true") Boolean isTemporary,
        @Schema(description = "등록일") OffsetDateTime createdAt,
        @Schema(description = "수정일") OffsetDateTime updatedAt,
        @Schema(description = "근속일수") Long tenureDays,
        @Schema(description = "근속개월") Integer tenureMonths,
        @Schema(description = "퇴직금 발생 요건 여부", example = "true") Boolean isSeverancePayEligible,
        @Schema(description = "통장사본 첨부", example = "true") Boolean hasBankbook,
        @Schema(description = "신분증 사본 첨부", example = "true") Boolean hasIdCard,
        @Schema(description = "서명이미지 첨부", example = "true") Boolean hasSignatureImage,
        @Schema(description = "기타 첨부", example = "true") Boolean hasFile) {

    public static LaborListResponse from(Labor labor) {
        return new LaborListResponse(
                labor.getId(),
                labor.getType().getLabel(),
                labor.getType(),
                labor.getTypeDescription(),
                labor.getName(),
                labor.getWorkType() != null ? labor.getWorkType().getLabel() : null,
                labor.getWorkType(),
                labor.getWorkTypeDescription(),
                labor.getIsHeadOffice(),
                labor.getMainWork(),
                labor.getDailyWage(),
                labor.getBankName(),
                labor.getAccountNumber(),
                labor.getAccountHolder(),
                labor.getHireDate(),
                labor.getResignationDate(),
                labor.getOutsourcingCompany() != null ? CompanySimpleResponse.from(labor.getOutsourcingCompany())
                        : null,
                labor.getPhoneNumber(),
                PrivacyMaskingUtils.maskResidentNumber(labor.getResidentNumber()),
                labor.getIsTemporary(),
                labor.getCreatedAt(),
                labor.getUpdatedAt(),
                labor.getTenureDays(),
                labor.getTenureMonths(),
                labor.getIsSeverancePayEligible(),
                labor.getHasBankbook(),
                labor.getHasIdCard(),
                labor.getHasSignatureImage(),
                labor.getHasFile());
    }
}
