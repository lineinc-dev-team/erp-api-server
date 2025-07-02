package com.lineinc.erp.api.server.presentation.v1.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Schema(description = "발주처 파일 정보")
public record ClientCompanyFileRequest(

        @NotBlank
        @Schema(description = "문서명", example = "계약서_2024")
        String documentName,

        @NotBlank
        @Schema(description = "파일 접근용 URL", example = "https://s3.amazonaws.com/bucket/contract_2024.pdf")
        String fileUrl,

        @NotBlank
        @Schema(description = "원본 파일명", example = "contract_2024.pdf")
        String originalFileName,

        @Schema(description = "파일에 대한 메모", example = "계약서 사본")
        String memo
) {
}