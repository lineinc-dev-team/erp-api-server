package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@ParameterObject
@Schema(description = "인력정보 엑셀 다운로드 요청")
public record LaborDownloadRequest(
        @NotBlank @Schema(description = "허용 필드: id, type, name, residentNumber, outsourcingCompanyName, workType, mainWork, phoneNumber, dailyWage, accountNumber, hireDate, resignationDate, tenureDays, tenureMonths, isSeverancePayEligible, hasBankbook, hasIdCard, hasSignatureImage, hasFile", example = "id, name, type") String fields) {
    public static final List<String> ALLOWED_FIELDS = List.of(
            "id", "type", "name", "residentNumber", "outsourcingCompanyName", "workType", "mainWork",
            "phoneNumber", "dailyWage", "accountNumber", "hireDate", "resignationDate", "tenureDays", "tenureMonths",
            "isSeverancePayEligible", "hasBankbook", "hasIdCard",
            "hasSignatureImage", "hasFile");
}
