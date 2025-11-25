package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request;

import java.util.List;
import com.lineinc.erp.api.server.domain.clientcompany.enums.ClientCompanyPaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "발주처 등록 요청")
public record ClientCompanyCreateRequest(
        @Schema(description = "발주처명", example = "삼성건설") @NotBlank String name,
        @Schema(description = "사업자등록번호", example = "123-45-67890") String businessNumber,
        @Schema(description = "대표자명", example = "홍길동") @NotBlank String ceoName,
        @Schema(description = "본사 주소", example = "서울시 강남구") @NotBlank String address,
        @Schema(description = "상세 주소", example = "강남구 테헤란로 123") String detailAddress,
        @Schema(description = "전화번호", example = "02-123-5678") @NotBlank String landlineNumber,
        @Schema(description = "팩스번호", example = "02-123-5679") String faxNumber,
        @Schema(description = "개인 휴대폰", example = "010-1234-5678") String phoneNumber,
        @Schema(description = "이메일", example = "example@samsung.com") @Email String email,
        @Schema(description = "결제 방식", example = "CASH") ClientCompanyPaymentMethod paymentMethod,
        @Schema(description = "결제 유예 기간", example = "2") String paymentPeriod,
        @Schema(description = "비고 / 메모") String memo,
        @Schema(description = "사용 여부", example = "true") @NotNull Boolean isActive,
        @Schema(description = "본사 담당자 ID", example = "1") Long userId,
        @Schema(description = "홈페이지 URL", example = "https://www.samsung.com") String homepageUrl,
        @Schema(description = "홈페이지 로그인 아이디", example = "admin") String homepageLoginId,
        @Schema(description = "홈페이지 패스워드", example = "password123") String homepagePassword,
        @Schema(description = "담당자 목록") @Valid List<ClientCompanyContactCreateRequest> contacts,
        @Schema(description = "파일 목록") @Valid List<ClientCompanyFileCreateRequest> files) {
}
