package com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response;

import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCost;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 현장관리비 응답
 */
@Schema(description = "현장관리비 응답")
public record SiteManagementCostResponse(

        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "년월", example = "2025-01") String yearMonth,

        @Schema(description = "현장 정보") SiteResponse.SiteSimpleResponse site,
        @Schema(description = "공정 정보") SiteProcessResponse.SiteProcessSimpleResponse siteProcess,

        // 현장관리비
        @Schema(description = "직원급여", example = "5000000") Long employeeSalary,
        @Schema(description = "퇴직연금(정규직)", example = "500000") Long regularRetirementPension,
        @Schema(description = "퇴직공제부금", example = "300000") Long retirementDeduction,
        @Schema(description = "4대보험(상용)", example = "450000") Long majorInsuranceRegular,
        @Schema(description = "4대보험(일용)", example = "200000") Long majorInsuranceDaily,
        @Schema(description = "보증수수료(계약보증)", example = "100000") Long contractGuaranteeFee,
        @Schema(description = "보증수수료(현장별건설기계)", example = "150000") Long equipmentGuaranteeFee,
        @Schema(description = "국세납부", example = "80000") Long nationalTaxPayment,
        @Schema(description = "현장관리비 합계", example = "6780000") Long siteManagementTotal,

        // 본사관리비
        @Schema(description = "본사관리비", example = "1000000") Long headquartersManagementCost) {

    public static SiteManagementCostResponse from(final SiteManagementCost entity) {
        return new SiteManagementCostResponse(
                entity.getId(),
                entity.getYearMonth(),
                entity.getSite() != null ? SiteResponse.SiteSimpleResponse.from(entity.getSite()) : null,
                entity.getSiteProcess() != null
                        ? SiteProcessResponse.SiteProcessSimpleResponse.from(entity.getSiteProcess())
                        : null,
                entity.getEmployeeSalary(),
                entity.getRegularRetirementPension(),
                entity.getRetirementDeduction(),
                entity.getMajorInsuranceRegular(),
                entity.getMajorInsuranceDaily(),
                entity.getContractGuaranteeFee(),
                entity.getEquipmentGuaranteeFee(),
                entity.getNationalTaxPayment(),
                entity.calculateSiteManagementTotal(),
                entity.getHeadquartersManagementCost());
    }
}
