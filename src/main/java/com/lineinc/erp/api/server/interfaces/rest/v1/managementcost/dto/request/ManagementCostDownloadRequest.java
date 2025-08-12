package com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@ParameterObject
@Schema(description = "관리비 엑셀 다운로드 요청")
public record ManagementCostDownloadRequest(
        @NotBlank
        @Schema(
                description = "허용 필드: id, siteName, processName, itemType, paymentDate, businessNumber, ceoName, accountNumber, accountHolder, supplyPrice, vat, total, hasFile ,memo",
                example = "id, siteName, processName"
        )
        String fields
) {
    public static final List<String> ALLOWED_FIELDS = List.of(
            "id", "siteName", "processName", "itemType", "paymentDate",
            "businessNumber", "ceoName", "accountNumber", "accountHolder",
            "supplyPrice", "vat", "total", "hasFile", "memo"
    );

}
