package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유류업체 정보 응답")
public record FuelCompanyResponse(
        @Schema(description = "유류집계 ID", example = "1") Long id,
        @Schema(description = "유류업체 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany) {

    public static FuelCompanyResponse from(final FuelAggregation fuelAggregation) {
        if (fuelAggregation == null) {
            return null;
        }

        return new FuelCompanyResponse(
                fuelAggregation.getId(),
                fuelAggregation.getOutsourcingCompany() != null
                        ? CompanyResponse.CompanySimpleResponse.from(fuelAggregation.getOutsourcingCompany())
                        : null);
    }
}
