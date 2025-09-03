package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request;

import com.lineinc.erp.api.server.domain.client.enums.FileType;
import com.lineinc.erp.api.server.shared.validation.MultiConstraint;
import com.lineinc.erp.api.server.shared.validation.ValidatorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "발주처 파일 수정 요청")
public record ClientCompanyFileUpdateRequest(
        @Schema(description = "파일 ID (수정 시 필수)", example = "123") @NotNull Long id,

        @Schema(description = "문서명", example = "계약서_2024") String name,

        @MultiConstraint(type = ValidatorType.URL) @Schema(description = "파일 접근용 URL", example = "https://s3.amazonaws.com/bucket/contract_2024.pdf") String fileUrl,

        @Schema(description = "원본 파일명", example = "contract_2024.pdf") String originalFileName,

        @Schema(description = "파일 타입", example = "BUSINESS_LICENSE") @NotNull FileType type,

        @Schema(description = "파일에 대한 메모", example = "계약서 사본") String memo) {
}