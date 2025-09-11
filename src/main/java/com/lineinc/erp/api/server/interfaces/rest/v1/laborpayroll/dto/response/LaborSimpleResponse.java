package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.labormanagement.enums.WorkType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;
import com.lineinc.erp.api.server.shared.util.PrivacyMaskingUtils;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 노무명세서용 인력 간단 응답 DTO
 */
@Schema(description = "인력 간단 정보")
public record LaborSimpleResponse(
        @Schema(description = "ID") Long id,
        @Schema(description = "구분") String type,
        @Schema(description = "구분 코드") LaborType typeCode,
        @Schema(description = "구분 설명") String typeDescription,
        @Schema(description = "이름") String name,
        @Schema(description = "주민번호") String residentNumber,
        @Schema(description = "주소") String address,
        @Schema(description = "상세주소") String detailAddress,
        @Schema(description = "계좌번호") String accountNumber,
        @Schema(description = "계좌명") String accountHolder,
        @Schema(description = "은행명") String bankName,
        @Schema(description = "소속 업체") CompanySimpleResponse outsourcingCompany,
        @Schema(description = "본사여부") Boolean isHeadOffice,
        @Schema(description = "공종") String workType,
        @Schema(description = "공종 코드") WorkType workTypeCode,
        @Schema(description = "공종 설명") String workTypeDescription,
        @Schema(description = "주작업") String mainWork) {

    /**
     * Labor 엔티티로부터 DTO 생성
     */
    public static LaborSimpleResponse from(Labor labor) {
        return new LaborSimpleResponse(
                labor.getId(),
                labor.getType() != null ? labor.getType().getLabel() : null,
                labor.getType(),
                labor.getTypeDescription(),
                labor.getName(),
                PrivacyMaskingUtils.maskResidentNumber(labor.getResidentNumber()),
                labor.getAddress(),
                labor.getDetailAddress(),
                labor.getAccountNumber(),
                labor.getAccountHolder(),
                labor.getBankName(),
                labor.getOutsourcingCompany() != null ? CompanySimpleResponse.from(labor.getOutsourcingCompany())
                        : null,
                labor.getIsHeadOffice(),
                labor.getWorkType() != null ? labor.getWorkType().getLabel() : null,
                labor.getWorkType(),
                labor.getWorkTypeDescription(),
                labor.getMainWork());
    }
}
