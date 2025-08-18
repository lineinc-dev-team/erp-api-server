package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@ParameterObject
@Schema(description = "강재 관리 엑셀 다운로드 요청")
public record SteelManagementDownloadRequest(
        @NotBlank @Schema(description = "허용 필드: id, siteName, processName, type, orderDate, approvalDate, releaseDate, startDateAndEndDate, outsourcingCompanyName, businessNumber, totalAmount, memo", example = "id, siteName") String fields) {
    public static final List<String> ALLOWED_FIELDS = List.of(
            "id", "siteName", "processName", "type", "orderDate", "approvalDate", "releaseDate",
            "startDateAndEndDate", "outsourcingCompanyName", "businessNumber", "totalAmount", "memo");

}
