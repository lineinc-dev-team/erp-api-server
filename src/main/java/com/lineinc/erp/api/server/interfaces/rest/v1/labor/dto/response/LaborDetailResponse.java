package com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.labor.enums.LaborWorkType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;
import com.lineinc.erp.api.server.shared.util.PrivacyMaskingUtils;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력정보 상세 응답")
public record LaborDetailResponse(
        @Schema(description = "인력 ID") Long id,
        @Schema(description = "노무 구분") String type,
        @Schema(description = "노무 구분 코드") LaborType typeCode,
        @Schema(description = "구분 설명") String typeDescription,
        @Schema(description = "이름") String name,
        @Schema(description = "공종") String workType,
        @Schema(description = "공종 코드") LaborWorkType workTypeCode,
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
        @Schema(description = "주민등록번호") String residentNumber,
        @Schema(description = "주소") String address,
        @Schema(description = "상세주소") String detailAddress,
        @Schema(description = "휴대폰 번호") String phoneNumber,
        @Schema(description = "비고") String memo,
        @Schema(description = "등록일") OffsetDateTime createdAt,
        @Schema(description = "수정일") OffsetDateTime updatedAt,
        @Schema(description = "근속개월") Integer tenureMonths,
        @Schema(description = "퇴직금 발생 요건 여부", example = "true") Boolean isSeverancePayEligible,
        @Schema(description = "임시 인력 여부", example = "true") Boolean isTemporary,
        @Schema(description = "첨부파일 목록") List<LaborFileResponse> files) {

    public static LaborDetailResponse from(final Labor labor) {
        final List<LaborFileResponse> fileResponses = labor.getFiles() != null ? labor.getFiles().stream()
                .map(LaborFileResponse::from)
                .collect(Collectors.toList()) : List.of();

        return new LaborDetailResponse(
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
                PrivacyMaskingUtils.maskResidentNumber(labor.getResidentNumber()),
                labor.getAddress(),
                labor.getDetailAddress(),
                labor.getPhoneNumber(),
                labor.getMemo(),
                labor.getCreatedAt(),
                labor.getUpdatedAt(),
                labor.getTenureMonths(),
                labor.getIsSeverancePayEligible(),
                labor.getIsTemporary(),
                fileResponses

        );
    }
}
