package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@ParameterObject
@Schema(description = "노무명세서 엑셀 다운로드 요청")
public record LaborPayrollDownloadRequest(
        @NotBlank @Schema(description = "허용 필드: id, siteName, processName, regularEmployeeCount, directContractCount, etcCount, totalLaborCost, totalDeductions, totalNetPayment, memo", example = "id, siteName, processName, totalLaborCost") String fields) {
    public static final List<String> ALLOWED_FIELDS = List.of(
            "id",
            "siteName",
            "processName",
            "regularEmployeeCount",
            "directContractCount",
            "etcCount",
            "totalLaborCost",
            "totalDeductions",
            "totalNetPayment",
            "memo");
}
