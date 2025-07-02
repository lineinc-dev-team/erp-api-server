package com.lineinc.erp.api.server.presentation.v1.client.dto;

import com.lineinc.erp.api.server.domain.client.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "발주처 등록 요청")
public record ClientCompanyCreateRequest(
        @NotBlank
        @Schema(description = "발주처명", example = "삼성건설")
        String name,

        @NotBlank
        @Schema(description = "사업자등록번호", example = "123-45-67890")
        String businessNumber,

        @NotBlank
        @Schema(description = "대표자명", example = "홍길동")
        String ceoName,

        @NotBlank
        @Schema(description = "본사 주소", example = "서울시 강남구")
        String address,

        @Schema(description = "전화번호 지역번호", example = "02")
        String areaCode,

        @Schema(description = "전화번호", example = "010-1234-5678")
        String phoneNumber,

        @Email
        @Schema(description = "이메일", example = "example@samsung.com")
        String email,

        @Schema(description = "결제 방식", example = "CASH")
        PaymentMethod paymentMethod,

        @Schema(description = "결제 유예 기간", example = "2")
        String paymentPeriod,

        @Schema(description = "비고 / 메모")
        String memo,

        @Schema(description = "사용 여부", example = "true")
        boolean isActive,

        @Valid
        @Schema(description = "담당자 목록")
        List<ClientCompanyContactCreateRequest> contacts,

        @Valid
        @Schema(description = "파일 목록")
        List<ClientCompanyFileRequest> files
) {
}
