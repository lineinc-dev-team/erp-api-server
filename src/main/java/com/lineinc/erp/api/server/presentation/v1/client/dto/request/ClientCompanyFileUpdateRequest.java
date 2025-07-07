package com.lineinc.erp.api.server.presentation.v1.client.dto.request;

import com.lineinc.erp.api.server.common.validation.MultiConstraint;
import com.lineinc.erp.api.server.common.validation.ValidatorType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "발주처 파일 등록 요청")
public record ClientCompanyFileUpdateRequest(
        @Schema(description = "파일 ID (수정 시 필수)", example = "123")
        Long id,

        @Schema(description = "문서명", example = "계약서_2024")
        String documentName,

        @MultiConstraint(type = ValidatorType.URL)
        @Schema(description = "파일 접근용 URL", example = "https://s3.amazonaws.com/bucket/contract_2024.pdf")
        String fileUrl,

        @Schema(description = "원본 파일명", example = "contract_2024.pdf")
        String originalFileName,

        @Schema(description = "파일에 대한 메모", example = "계약서 사본")
        String memo
) {
}