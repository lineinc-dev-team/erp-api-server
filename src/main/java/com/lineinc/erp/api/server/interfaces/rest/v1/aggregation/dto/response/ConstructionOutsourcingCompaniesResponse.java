package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 외주(공사) 외주업체 목록 응답
 */
@Schema(description = "외주(공사) 외주업체 목록 응답")
public record ConstructionOutsourcingCompaniesResponse(
        @Schema(description = "외주업체 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany,
        @Schema(description = "외주업체 계약명", example = "2025년 공사 계약") String contractName) {

    public static ConstructionOutsourcingCompaniesResponse from(final OutsourcingCompany outsourcingCompany,
            final OutsourcingCompanyContract outsourcingCompanyContract) {
        return new ConstructionOutsourcingCompaniesResponse(
                CompanyResponse.CompanySimpleResponse.from(outsourcingCompany),
                outsourcingCompanyContract != null ? outsourcingCompanyContract.getContractName() : null);
    }
}
