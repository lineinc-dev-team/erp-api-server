package com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@ParameterObject
@Schema(description = "자재관리 엑셀 다운로드 요청")
public record MaterialManagementDownloadRequest(
        @NotBlank
        @Schema(
                description = "허용 필드: id, siteName, processName, inputType, deliveryDate, name, " +
                        "standard, usage, quantity, unitPrice, supplyPrice, vat, total, hasFile, memo",
                example = "siteName,processName,inputType"
        )
        String fields
) {
    public static final List<String> ALLOWED_FIELDS = List.of(
            "id", "siteName", "processName", "inputType", "deliveryDate", "name",
            "standard", "usage", "quantity", "unitPrice", "supplyPrice", "vat", "total", "hasFile", "memo"
    );

}
