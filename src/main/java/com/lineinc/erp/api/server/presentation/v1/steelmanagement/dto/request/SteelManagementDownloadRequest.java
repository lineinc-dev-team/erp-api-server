package com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@ParameterObject
@Schema(description = "강재 관리 엑셀 다운로드 요청")
public record SteelManagementDownloadRequest(
        @NotBlank
        @Schema(
                description = "허용 필드: id, siteName, processName, paymentDate, standard, name, unit, count, length, totalLength, unitWeight," +
                        "quantity, unitPrice, supplyPrice, usage, hasFile, type, memo",
                example = "id, siteName"
        )
        String fields
) {
    public static final List<String> ALLOWED_FIELDS = List.of(
            "id", "siteName", "processName", "paymentDate", "standard", "name", "unit", "count",
            "length", "totalLength", "unitWeight", "quantity", "unitPrice", "supplyPrice",
            "usage", "hasFile", "type", "memo"
    );

}
