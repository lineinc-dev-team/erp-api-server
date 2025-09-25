package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@ParameterObject
@Schema(description = "관리비 엑셀 다운로드 요청")
public record ManagementCostDownloadRequest(
        @NotBlank @Schema(description = "허용 필드: id, siteName, processName, itemType, paymentDate, outsourcingCompanyName, outsourcingCompanyBusinessNumber, outsourcingCompanyCeoName, outsourcingCompanyAccountNumber, outsourcingCompanyAccountHolder, supplyPrice, vat, total, hasFile, memo, deductibleAmount", example = "id, siteName, processName") String fields) {
    public static final List<String> ALLOWED_FIELDS = List.of(
            "id", "siteName", "processName", "itemType", "paymentDate",
            "outsourcingCompanyName", "outsourcingCompanyBusinessNumber", "outsourcingCompanyCeoName",
            "outsourcingCompanyAccountNumber", "outsourcingCompanyAccountHolder",
            "supplyPrice", "vat", "total", "hasFile", "memo", "deductibleAmount");

}
