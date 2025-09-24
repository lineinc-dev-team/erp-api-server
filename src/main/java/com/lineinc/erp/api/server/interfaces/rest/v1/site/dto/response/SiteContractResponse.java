package com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.lineinc.erp.api.server.domain.site.entity.SiteContract;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "현장 계약 정보 응답")
public record SiteContractResponse(
        @Schema(description = "계약 ID", example = "1") Long id,
        @Schema(description = "계약명", example = "전기공사 계약") String name,
        @Schema(description = "계약 금액", example = "120000000") Long amount,
        @Schema(description = "비고", example = "1차 계약 건") String memo,
        @Schema(description = "공급가", example = "13636364") Long supplyPrice,
        @Schema(description = "부가세", example = "1363636") Long vat,
        @Schema(description = "매입세", example = "1000000") Long purchaseTax,
        @Schema(description = "생성자", example = "홍길동") String createdBy,
        @Schema(description = "생성일", example = "2024-01-01T09:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "계약 관련 파일 목록") List<SiteFileResponse> files) {
    public static SiteContractResponse from(final SiteContract contract) {
        final List<SiteFileResponse> fileResponses = contract.getFiles().stream()
                .map(SiteFileResponse::from)
                .collect(Collectors.toList());
        return new SiteContractResponse(
                contract.getId(),
                contract.getName(),
                contract.getAmount(),
                contract.getMemo(),
                contract.getSupplyPrice(),
                contract.getVat(),
                contract.getPurchaseTax(),
                contract.getCreatedBy(),
                contract.getCreatedAt(),
                fileResponses);
    }
}
