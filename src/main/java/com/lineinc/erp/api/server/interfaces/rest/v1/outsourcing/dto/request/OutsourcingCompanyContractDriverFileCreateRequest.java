package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractDriverDocumentType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "외주업체 계약 드라이버 파일 등록 요청")
public record OutsourcingCompanyContractDriverFileCreateRequest(
        @Schema(description = "서류 타입", example = "DRIVER_LICENSE") @NotNull OutsourcingCompanyContractDriverDocumentType documentType,

        @Schema(description = "파일 URL", example = "https://example.com/files/driver_license.pdf") @NotBlank String fileUrl,

        @Schema(description = "원본 파일명", example = "driver_license.pdf") @NotBlank String originalFileName) {
}
