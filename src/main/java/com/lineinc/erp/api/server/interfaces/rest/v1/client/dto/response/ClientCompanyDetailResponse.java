package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.enums.PaymentMethod;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "발주처 정보 상세 응답")
public record ClientCompanyDetailResponse(

        @Schema(description = "발주처 ID", example = "123") Long id,

        @Schema(description = "사업자등록번호", example = "123-45-67890") String businessNumber,

        @Schema(description = "발주처명", example = "삼성건설") String name,

        @Schema(description = "대표자명", example = "홍길동") String ceoName,

        @Schema(description = "주소", example = "서울시 강남구") String address,

        @Schema(description = "상세 주소", example = "강남구 테헤란로 123") String detailAddress,

        @Schema(description = "전화번호", example = "02-1234-5678") String landlineNumber,

        @Schema(description = "개인 휴대폰", example = "010-1234-5678") String phoneNumber,

        @Schema(description = "이메일 주소", example = "client@example.com") String email,

        @Schema(description = "결제 방식", example = "현금") String paymentMethod,

        @Schema(description = "결제 방식 코드", example = "CASH") PaymentMethod paymentMethodCode,

        @Schema(description = "결제 유예 기간", example = "2") String paymentPeriod,

        @Schema(description = "사용 여부", example = "true") Boolean isActive,

        @Schema(description = "등록일") OffsetDateTime createdAt,

        @Schema(description = "수정일") OffsetDateTime updatedAt,

        @Schema(description = "비고", example = "기타 메모") String memo,

        @Schema(description = "발주처 담당자 목록") List<ClientCompanyContactResponse> contacts,

        @Schema(description = "발주처 파일 목록") List<ClientCompanyFileResponse> files,

        @Schema(description = "본사 담당자") UserResponse.UserSimpleResponse user) {
    public static ClientCompanyDetailResponse from(ClientCompany clientCompany) {
        return new ClientCompanyDetailResponse(
                clientCompany.getId(),
                clientCompany.getBusinessNumber(),
                clientCompany.getName(),
                clientCompany.getCeoName(),
                clientCompany.getAddress(),
                clientCompany.getDetailAddress(),
                clientCompany.getLandlineNumber(),
                clientCompany.getPhoneNumber(),
                clientCompany.getEmail(),
                clientCompany.getPaymentMethod().getDisplayName(),
                clientCompany.getPaymentMethod(),
                clientCompany.getPaymentPeriod(),
                clientCompany.isActive(),
                clientCompany.getCreatedAt(),
                clientCompany.getUpdatedAt(),
                clientCompany.getMemo(),
                clientCompany.getContacts().stream()
                        .map(ClientCompanyContactResponse::from)
                        .collect(Collectors.toList()),
                clientCompany.getFiles().stream()
                        .map(ClientCompanyFileResponse::from)
                        .collect(Collectors.toList()),
                clientCompany.getUser() != null ? UserResponse.UserSimpleResponse.from(clientCompany.getUser()) : null);
    }

}