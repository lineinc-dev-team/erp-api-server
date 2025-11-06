package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetailEquipment;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractDriverResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리비 식대 상세 - 장비기사 정보 응답")
public record ManagementCostMealFeeDetailEquipmentResponse(
        @Schema(description = "식대 상세 ID", example = "1") Long id,
        @Schema(description = "조식 개수", example = "3") Integer breakfastCount,
        @Schema(description = "중식 개수", example = "5") Integer lunchCount,
        @Schema(description = "석식 개수", example = "5") Integer dinnerCount,
        @Schema(description = "단가", example = "8000") Long unitPrice,
        @Schema(description = "금액", example = "80000") Long amount,
        @Schema(description = "비고", example = "현장 식대") String memo,
        @Schema(description = "외주업체 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany,
        @Schema(description = "기사 정보") ContractDriverResponse.ContractDriverSimpleResponse driver) {
    public static ManagementCostMealFeeDetailEquipmentResponse from(
            final ManagementCostMealFeeDetailEquipment detail) {
        return new ManagementCostMealFeeDetailEquipmentResponse(
                detail.getId(),
                detail.getBreakfastCount(),
                detail.getLunchCount(),
                detail.getDinnerCount(),
                detail.getUnitPrice(),
                detail.getAmount(),
                detail.getMemo(),
                detail.getOutsourcingCompany() != null
                        ? CompanyResponse.CompanySimpleResponse.from(detail.getOutsourcingCompany())
                        : null,
                detail.getOutsourcingCompanyContractDriver() != null
                        ? ContractDriverResponse.ContractDriverSimpleResponse
                                .from(detail.getOutsourcingCompanyContractDriver())
                        : null);
    }
}

