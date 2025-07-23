package com.lineinc.erp.api.server.presentation.v1.site.dto.response;

import com.lineinc.erp.api.server.domain.site.entity.SiteContract;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "현장 계약 정보 응답")
public record SiteContractResponse(

        @Schema(description = "계약 ID", example = "1")
        Long id,

        @Schema(description = "계약명", example = "전기공사 계약")
        String name,

        @Schema(description = "계약 금액", example = "120000000")
        Long amount,

        @Schema(description = "비고", example = "1차 계약 건")
        String memo,

        @Schema(description = "계약 관련 파일 목록")
        List<SiteFileResponse> files

) {
    public static SiteContractResponse from(SiteContract contract) {
        List<SiteFileResponse> fileResponses = contract.getFiles().stream()
                .map(SiteFileResponse::from)
                .collect(Collectors.toList());
        return new SiteContractResponse(
                contract.getId(),
                contract.getName(),
                contract.getAmount(),
                contract.getMemo(),
                fileResponses
        );
    }
}
