package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@ParameterObject
@Schema(description = "외주업체 계약 엑셀 다운로드 요청")
public record ContractDownloadRequest(
        @NotEmpty @Schema(description = "허용 필드: id, siteName, processName, companyName, businessNumber, contractType, contractPeriod, contractAmount, defaultDeductions, taxInvoiceCondition, contacts, createdAt, contractStatus, memo", example = "id,siteName,processName") String fields) {

    public static final List<String> ALLOWED_FIELDS = List.of(
            "id", "siteName", "processName", "companyName", "businessNumber", "contractType",
            "contractPeriod", "contractAmount", "defaultDeductions", "taxInvoiceCondition",
            "contacts", "createdAt", "contractStatus", "memo");
}
