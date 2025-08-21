package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.labormanagement.enums.WorkType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력정보 목록 응답")
public record LaborListResponse(
        @Schema(description = "인력 ID") Long id,
        @Schema(description = "노무 구분") String type,
        @Schema(description = "노무 구분 코드") LaborType typeCode,
        @Schema(description = "구분 설명") String typeDescription,
        @Schema(description = "이름") String name,
        @Schema(description = "공종") WorkType workType,
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
        @Schema(description = "등록일") OffsetDateTime createdAt,
        @Schema(description = "수정일") OffsetDateTime updatedAt,
        @Schema(description = "첨부파일 존재 여부", example = "true") Boolean hasFile) {

    public static LaborListResponse from(Labor labor) {
        return new LaborListResponse(
                labor.getId(),
                labor.getType().getLabel(),
                labor.getType(),
                labor.getTypeDescription(),
                labor.getName(),
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
                labor.getResidentNumber(),
                labor.getCreatedAt(),
                labor.getUpdatedAt(),
                labor.getHasFile());
    }
}
