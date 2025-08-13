package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractDriverDocumentType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 드라이버 파일 수정 요청")
public record OutsourcingCompanyContractDriverFileUpdateRequest(
        @Schema(description = "파일 ID", example = "1") Long id,

        @Schema(description = "서류 타입", example = "DRIVER_LICENSE") OutsourcingCompanyContractDriverDocumentType documentType,

        @Schema(description = "파일 URL", example = "https://example.com/files/driver_license.pdf") String fileUrl,

        @Schema(description = "원본 파일명", example = "driver_license.pdf") String originalFileName) {
}
