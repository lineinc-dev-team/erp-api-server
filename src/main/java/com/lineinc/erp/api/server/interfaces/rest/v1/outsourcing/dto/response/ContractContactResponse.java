package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractContact;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 담당자 응답")
public record ContractContactResponse(
        @Schema(description = "ID", example = "101") Long id,
        @Schema(description = "담당자명", example = "홍길동") String name,
        @Schema(description = "개인 휴대폰", example = "010-9876-5432") String phoneNumber,
        @Schema(description = "이메일", example = "honggildong@example.com") String email,
        @Schema(description = "부서", example = "영업팀") String department,
        @Schema(description = "직급", example = "과장") String position,
        @Schema(description = "전화번호", example = "02-1234-5678") String landlineNumber,
        @Schema(description = "비고", example = "주요 거래처 담당") String memo,
        @Schema(description = "대표 담당자 여부", example = "true") Boolean isMain) {
    public static ContractContactResponse from(OutsourcingCompanyContractContact contact) {
        return new ContractContactResponse(
                contact.getId(),
                contact.getName(),
                contact.getPhoneNumber(),
                contact.getEmail(),
                contact.getDepartment(),
                contact.getPosition(),
                contact.getLandlineNumber(),
                contact.getMemo(),
                contact.getIsMain());
    }
}
