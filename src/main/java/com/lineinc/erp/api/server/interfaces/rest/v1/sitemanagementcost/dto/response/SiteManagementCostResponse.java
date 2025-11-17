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
        @Schema(description = "직원급여 공급가", example = "4545455") Long employeeSalarySupplyPrice,
        @Schema(description = "직원급여 부가세", example = "454545") Long employeeSalaryVat,
        @Schema(description = "직원급여 공제금액", example = "0") Long employeeSalaryDeduction,
        @Schema(description = "퇴직연금(정규직)", example = "500000") Long regularRetirementPension,
        @Schema(description = "퇴직연금(정규직) 공급가", example = "454545") Long regularRetirementPensionSupplyPrice,
        @Schema(description = "퇴직연금(정규직) 부가세", example = "45455") Long regularRetirementPensionVat,
        @Schema(description = "퇴직연금(정규직) 공제금액", example = "0") Long regularRetirementPensionDeduction,
        @Schema(description = "퇴직공제부금", example = "300000") Long retirementDeduction,
        @Schema(description = "퇴직공제부금 공급가", example = "272727") Long retirementDeductionSupplyPrice,
        @Schema(description = "퇴직공제부금 부가세", example = "27273") Long retirementDeductionVat,
        @Schema(description = "퇴직공제부금 공제금액", example = "0") Long retirementDeductionDeduction,
        @Schema(description = "4대보험(상용)", example = "450000") Long majorInsuranceRegular,
        @Schema(description = "4대보험(상용) 공급가", example = "409091") Long majorInsuranceRegularSupplyPrice,
        @Schema(description = "4대보험(상용) 부가세", example = "40909") Long majorInsuranceRegularVat,
        @Schema(description = "4대보험(상용) 공제금액", example = "0") Long majorInsuranceRegularDeduction,
        @Schema(description = "4대보험(일용)", example = "200000") Long majorInsuranceDaily,
        @Schema(description = "4대보험(일용) 공급가", example = "181818") Long majorInsuranceDailySupplyPrice,
        @Schema(description = "4대보험(일용) 부가세", example = "18182") Long majorInsuranceDailyVat,
        @Schema(description = "4대보험(일용) 공제금액", example = "0") Long majorInsuranceDailyDeduction,
        @Schema(description = "보증수수료(계약보증)", example = "100000") Long contractGuaranteeFee,
        @Schema(description = "보증수수료(계약보증) 공급가", example = "90909") Long contractGuaranteeFeeSupplyPrice,
        @Schema(description = "보증수수료(계약보증) 부가세", example = "9091") Long contractGuaranteeFeeVat,
        @Schema(description = "보증수수료(계약보증) 공제금액", example = "0") Long contractGuaranteeFeeDeduction,
        @Schema(description = "보증수수료(현장별건설기계)", example = "150000") Long equipmentGuaranteeFee,
        @Schema(description = "보증수수료(현장별건설기계) 공급가", example = "136364") Long equipmentGuaranteeFeeSupplyPrice,
        @Schema(description = "보증수수료(현장별건설기계) 부가세", example = "13636") Long equipmentGuaranteeFeeVat,
        @Schema(description = "보증수수료(현장별건설기계) 공제금액", example = "0") Long equipmentGuaranteeFeeDeduction,
        @Schema(description = "국세납부", example = "80000") Long nationalTaxPayment,
        @Schema(description = "국세납부 공급가", example = "72727") Long nationalTaxPaymentSupplyPrice,
        @Schema(description = "국세납부 부가세", example = "7273") Long nationalTaxPaymentVat,
        @Schema(description = "국세납부 공제금액", example = "0") Long nationalTaxPaymentDeduction,
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
                entity.getEmployeeSalarySupplyPrice(),
                entity.getEmployeeSalaryVat(),
                entity.getEmployeeSalaryDeduction(),
                entity.getRegularRetirementPension(),
                entity.getRegularRetirementPensionSupplyPrice(),
                entity.getRegularRetirementPensionVat(),
                entity.getRegularRetirementPensionDeduction(),
                entity.getRetirementDeduction(),
                entity.getRetirementDeductionSupplyPrice(),
                entity.getRetirementDeductionVat(),
                entity.getRetirementDeductionDeduction(),
                entity.getMajorInsuranceRegular(),
                entity.getMajorInsuranceRegularSupplyPrice(),
                entity.getMajorInsuranceRegularVat(),
                entity.getMajorInsuranceRegularDeduction(),
                entity.getMajorInsuranceDaily(),
                entity.getMajorInsuranceDailySupplyPrice(),
                entity.getMajorInsuranceDailyVat(),
                entity.getMajorInsuranceDailyDeduction(),
                entity.getContractGuaranteeFee(),
                entity.getContractGuaranteeFeeSupplyPrice(),
                entity.getContractGuaranteeFeeVat(),
                entity.getContractGuaranteeFeeDeduction(),
                entity.getEquipmentGuaranteeFee(),
                entity.getEquipmentGuaranteeFeeSupplyPrice(),
                entity.getEquipmentGuaranteeFeeVat(),
                entity.getEquipmentGuaranteeFeeDeduction(),
                entity.getNationalTaxPayment(),
                entity.getNationalTaxPaymentSupplyPrice(),
                entity.getNationalTaxPaymentVat(),
                entity.getNationalTaxPaymentDeduction(),
                entity.calculateSiteManagementTotal(),
                entity.getHeadquartersManagementCost());
    }
}
