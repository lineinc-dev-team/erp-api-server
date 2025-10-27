package com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response;

import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCost;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 현장관리비 응답
 */
@Schema(description = "현장관리비 응답")
public record SiteManagementCostResponse(

        @Schema(description = "ID", example = "1") Long id,

        @Schema(description = "년월", example = "2025-01") String yearMonth,

        @Schema(description = "현장 ID", example = "1") Long siteId,

        @Schema(description = "현장명", example = "서울 APT 신축공사") String siteName,

        @Schema(description = "공정 ID", example = "1") Long siteProcessId,

        @Schema(description = "공정명", example = "토공사") String siteProcessName,

        // 현장관리비
        @Schema(description = "직원급여", example = "5000000") Long employeeSalary,
        @Schema(description = "직원급여 메모") String employeeSalaryMemo,

        @Schema(description = "퇴직연금(정규직)", example = "500000") Long regularRetirementPension,
        @Schema(description = "퇴직연금(정규직) 메모") String regularRetirementPensionMemo,

        @Schema(description = "퇴직공제부금", example = "300000") Long retirementDeduction,
        @Schema(description = "퇴직공제부금 메모") String retirementDeductionMemo,

        @Schema(description = "4대보험(상용)", example = "450000") Long majorInsuranceRegular,
        @Schema(description = "4대보험(상용) 메모") String majorInsuranceRegularMemo,

        @Schema(description = "4대보험(일용)", example = "200000") Long majorInsuranceDaily,
        @Schema(description = "4대보험(일용) 메모") String majorInsuranceDailyMemo,

        @Schema(description = "보증수수료(계약보증)", example = "100000") Long contractGuaranteeFee,
        @Schema(description = "보증수수료(계약보증) 메모") String contractGuaranteeFeeMemo,

        @Schema(description = "보증수수료(현장별건설기계)", example = "150000") Long equipmentGuaranteeFee,
        @Schema(description = "보증수수료(현장별건설기계) 메모") String equipmentGuaranteeFeeMemo,

        @Schema(description = "국세납부", example = "80000") Long nationalTaxPayment,
        @Schema(description = "국세납부 메모") String nationalTaxPaymentMemo,

        @Schema(description = "현장관리비 합계", example = "6780000") Long siteManagementTotal,

        // 본사관리비
        @Schema(description = "본사관리비", example = "1000000") Long headquartersManagementCost,
        @Schema(description = "본사관리비 메모") String headquartersManagementCostMemo,

        @Schema(description = "전체 관리비 합계", example = "7780000") Long totalManagementCost) {

    public static SiteManagementCostResponse from(final SiteManagementCost entity) {
        return new SiteManagementCostResponse(
                entity.getId(),
                entity.getYearMonth(),
                entity.getSite() != null ? entity.getSite().getId() : null,
                entity.getSite() != null ? entity.getSite().getName() : null,
                entity.getSiteProcess() != null ? entity.getSiteProcess().getId() : null,
                entity.getSiteProcess() != null ? entity.getSiteProcess().getName() : null,
                entity.getEmployeeSalary(),
                entity.getEmployeeSalaryMemo(),
                entity.getRegularRetirementPension(),
                entity.getRegularRetirementPensionMemo(),
                entity.getRetirementDeduction(),
                entity.getRetirementDeductionMemo(),
                entity.getMajorInsuranceRegular(),
                entity.getMajorInsuranceRegularMemo(),
                entity.getMajorInsuranceDaily(),
                entity.getMajorInsuranceDailyMemo(),
                entity.getContractGuaranteeFee(),
                entity.getContractGuaranteeFeeMemo(),
                entity.getEquipmentGuaranteeFee(),
                entity.getEquipmentGuaranteeFeeMemo(),
                entity.getNationalTaxPayment(),
                entity.getNationalTaxPaymentMemo(),
                entity.calculateSiteManagementTotal(),
                entity.getHeadquartersManagementCost(),
                entity.getHeadquartersManagementCostMemo(),
                entity.calculateTotalManagementCost());
    }
}
