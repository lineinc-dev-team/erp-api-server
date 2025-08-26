package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportFuel;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.ContractDriverResponse.ContractDriverSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.ContractEquipmentResponse.ContractEquipmentSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 유류 응답")
public record DailyReportFuelResponse(
        @Schema(description = "ID", example = "1") Long id,

        @Schema(description = "유종", example = "경유") String fuelType,

        @Schema(description = "주유량", example = "100") Long fuelAmount,

        @Schema(description = "비고", example = "오전 주유") String memo,

        @Schema(description = "업체 정보") CompanySimpleResponse outsourcingCompany,

        @Schema(description = "기사 정보") ContractDriverSimpleResponse outsourcingCompanyDriver,

        @Schema(description = "장비 정보") ContractEquipmentSimpleResponse outsourcingCompanyEquipment) {

    public static DailyReportFuelResponse from(DailyReportFuel fuel) {
        return new DailyReportFuelResponse(
                fuel.getId(),
                fuel.getFuelType(),
                fuel.getFuelAmount(),
                fuel.getMemo(),
                fuel.getOutsourcingCompanyContract() != null
                        && fuel.getOutsourcingCompanyContract().getOutsourcingCompany() != null
                                ? CompanySimpleResponse
                                        .from(fuel.getOutsourcingCompanyContract().getOutsourcingCompany())
                                : null,
                fuel.getOutsourcingCompanyContractDriver() != null
                        ? ContractDriverSimpleResponse.from(fuel.getOutsourcingCompanyContractDriver())
                        : null,
                fuel.getOutsourcingCompanyContractEquipment() != null
                        ? ContractEquipmentSimpleResponse.from(fuel.getOutsourcingCompanyContractEquipment())
                        : null);
    }
}
