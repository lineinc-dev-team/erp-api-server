package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@ParameterObject
@Schema(description = "발주처 엑셀 다운로드 요청")
public record ClientCompanyDownloadRequest(
        @NotBlank @Schema(description = "허용 필드: id, businessNumber, name, email, ceoName, address, phoneNumber, landlineNumber, contactName, contactPositionAndDepartment, contactLandlineNumberAndEmail, userName, isActive, createdAtAndUpdatedAt, hasFile, memo", example = "id,businessNumber,isActive") String fields) {
    public static final List<String> ALLOWED_FIELDS = List.of(
            "id", "businessNumber", "name", "email", "ceoName", "address",
            "phoneNumber", "landlineNumber", "contactName", "contactPositionAndDepartment",
            "contactLandlineNumberAndEmail", "userName", "isActive", "createdAtAndUpdatedAt",
            "hasFile", "memo");
}
