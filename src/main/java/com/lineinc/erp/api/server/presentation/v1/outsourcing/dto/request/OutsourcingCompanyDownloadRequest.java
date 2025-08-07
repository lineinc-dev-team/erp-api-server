package com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@ParameterObject
@Schema(description = "발주처 엑셀 다운로드 요청")
public record OutsourcingCompanyDownloadRequest(
        @NotBlank
        @Schema(
                description = "허용 필드: id, name, businessNumber, type, ceoName, address, phoneNumber, landlineNumber, contactName, contactPositionAndDepartment, defaultDeductions, isActive, createdAtAndUpdatedAt, hasFile, memo, email",
                example = "id,businessNumber,isActive"
        )
        String fields
) {
    public static final List<String> ALLOWED_FIELDS = List.of(
            "id", "name", "businessNumber", "type", "ceoName", "address",
            "phoneNumber", "landlineNumber", "contactName", "contactPositionAndDepartment",
            "defaultDeductions", "isActive", "createdAtAndUpdatedAt", "hasFile", "memo", "email"
    );
}
