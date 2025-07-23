package com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "관리비 등록 요청")
public record ManagementCostCreateRequest(

        @NotNull
        @Schema(description = "현장 ID", example = "1")
        Long siteId,

        @NotNull
        @Schema(description = "공정 ID", example = "10")
        Long siteProcessId,

        @NotNull
        @Schema(description = "관리비 품목 구분", example = "자재비")
        String type,

        @NotNull
        @Schema(description = "결제일자", example = "2024-07-15T00:00:00+09:00")
        OffsetDateTime paymentDate,

        @Schema(description = "사업자등록번호", example = "123-45-67890")
        String businessNumber,

        @Schema(description = "대표자 이름", example = "홍길동")
        String ceoName,

        @Schema(description = "계좌번호", example = "1002-123-456789")
        String accountNumber,

        @Schema(description = "예금주", example = "홍길동")
        String accountHolder,

        @Schema(description = "비고", example = "기성 1회차 비용")
        String memo,
        
        @Schema(description = "관리비 상세 품목 목록")
        List<ManagementCostDetailCreateRequest> details,

        @Schema(description = "관리비 파일 목록")
        List<ManagementCostFileCreateRequest> files
) {
}
