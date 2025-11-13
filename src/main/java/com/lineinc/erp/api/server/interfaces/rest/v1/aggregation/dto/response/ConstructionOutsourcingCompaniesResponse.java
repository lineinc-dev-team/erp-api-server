package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractListResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 외주(공사) 외주업체 목록 응답
 */
@Schema(description = "외주(공사) 외주업체 목록 응답")
public record ConstructionOutsourcingCompaniesResponse(
        @Schema(description = "외주업체 계약 정보") ContractListResponse.ContractSimpleResponse outsourcingCompanyContract) {

    public static ConstructionOutsourcingCompaniesResponse from(
            final OutsourcingCompanyContract outsourcingCompanyContract) {
        return new ConstructionOutsourcingCompaniesResponse(
                ContractListResponse.ContractSimpleResponse.from(outsourcingCompanyContract));
    }
}
