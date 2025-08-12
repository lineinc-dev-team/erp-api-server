package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

import com.lineinc.erp.api.server.domain.managementcost.entity.ItemType;

@Schema(description = "관리비 수정 요청")
public record ManagementCostUpdateRequest(
        @Schema(description = "현장 ID", example = "1")
        Long siteId,

        @Schema(description = "공정 ID", example = "10")
        Long siteProcessId,

        @Schema(description = "관리비 품목 구분", example = "DEPOSIT")
        ItemType itemType,

        @Schema(description = "관리비 품목 설명", example = "6월 전기요금")
        String itemDescription,

        @Schema(description = "결제일자", example = "2024-07-15")
        LocalDate paymentDate,

        @Schema(description = "사업자등록번호", example = "123-45-67890")
        String businessNumber,

        @Schema(description = "대표자 이름", example = "홍길동")
        String ceoName,

        @Schema(description = "계좌번호", example = "1002-123-456789")
        String accountNumber,

        @Schema(description = "예금주", example = "홍길동")
        String accountHolder,

        @Schema(description = "은행명", example = "기업은행")
        String bankName,

        @Schema(description = "비고", example = "기성 1회차 비용")
        String memo,

        @Schema(description = "관리비 상세 품목 목록")
        List<ManagementCostDetailUpdateRequest> details,

        @Schema(description = "관리비 파일 목록")
        List<ManagementCostFileUpdateRequest> files
) {
}
