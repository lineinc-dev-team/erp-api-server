package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "외주업체 기본 정보")
public record OutsourcingCompanyBasicInfoRequest(
        @Schema(description = "업체명", example = "삼성건설") @NotNull String name,

        @Schema(description = "사업자등록번호", example = "123-45-67890") @NotNull String businessNumber,

        @Schema(description = "대표자명", example = "홍길동") @NotNull String ceoName,

        @Schema(description = "은행명", example = "신한은행") @NotNull String bankName,

        @Schema(description = "계좌번호", example = "123-456-789012") @NotNull String accountNumber,

        @Schema(description = "예금주", example = "홍길동") @NotNull String accountHolder,

        @Schema(description = "비고", example = "신규 거래처") String memo) {
}
