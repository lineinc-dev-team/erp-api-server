package com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@ParameterObject
@Schema(description = "현장관리비 엑셀 다운로드 요청")
public record SiteManagementCostDownloadRequest(
        @NotBlank @Schema(description = "허용 필드: id, yearMonth, siteName, siteProcessName, employeeSalary, regularRetirementPension, retirementDeduction, majorInsurance, contractGuaranteeFee, equipmentGuaranteeFee, nationalTaxPayment, siteManagementTotal, headquartersManagementCost", example = "id, yearMonth, siteName, siteProcessName") String fields) {
    public static final List<String> ALLOWED_FIELDS = List.of(
            "id", "yearMonth", "siteName", "siteProcessName",
            "employeeSalary", "regularRetirementPension", "retirementDeduction",
            "majorInsurance", "contractGuaranteeFee", "equipmentGuaranteeFee", "nationalTaxPayment",
            "siteManagementTotal", "headquartersManagementCost");
}
