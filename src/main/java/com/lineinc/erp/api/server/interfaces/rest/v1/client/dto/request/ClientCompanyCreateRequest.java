package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request;

import java.util.List;

import com.lineinc.erp.api.server.domain.client.enums.ClientCompanyPaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "발주처 등록 요청")
public record ClientCompanyCreateRequest(
        @NotBlank @Schema(description = "발주처명", example = "삼성건설") String name,
        @NotBlank @Schema(description = "사업자등록번호", example = "123-45-67890") String businessNumber,
        @NotBlank @Schema(description = "대표자명", example = "홍길동") String ceoName,
        @NotBlank @Schema(description = "본사 주소", example = "서울시 강남구") String address,
        @NotBlank @Schema(description = "상세 주소", example = "강남구 테헤란로 123") String detailAddress,
        @NotBlank @Schema(description = "전화번호", example = "02-123-5678") String landlineNumber,
        @NotBlank @Schema(description = "개인 휴대폰", example = "010-1234-5678") String phoneNumber,
        @NotBlank @Email @Schema(description = "이메일", example = "example@samsung.com") String email,
        @NotNull @Schema(description = "결제 방식", example = "CASH") ClientCompanyPaymentMethod paymentMethod,
        @NotBlank @Schema(description = "결제 유예 기간", example = "2") String paymentPeriod,
        @Schema(description = "비고 / 메모") String memo,
        @NotNull @Schema(description = "사용 여부", example = "true") boolean isActive,
        @NotNull @Schema(description = "본사 담당자 ID", example = "1") Long userId,
        @Valid @Schema(description = "담당자 목록") List<ClientCompanyContactCreateRequest> contacts,
        @Valid @Schema(description = "파일 목록") List<ClientCompanyFileCreateRequest> files) {
}
