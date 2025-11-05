package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@ParameterObject
@Schema(description = "외주업체 계약 엑셀 다운로드 요청")
public record ContractDownloadRequest(
        @NotEmpty @Schema(description = "다운로드할 필드들을 쉼표로 구분", example = EXAMPLE_FIELDS) String fields) {
    private static final String EXAMPLE_FIELDS = "id,siteName,processName,companyName,businessNumber,contractType,contractPeriod,contractAmount,defaultDeductions,taxInvoiceCondition,contactName,createdAt,contractStatus,memo,hasGuaranteeCertificate,hasContractCertificate";
    public static final List<String> ALLOWED_FIELDS = List.of(EXAMPLE_FIELDS.split(","));
}
