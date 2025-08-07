package com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;

import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyDefaultDeductionsType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "외주업체 상세 응답")
public record OutsourcingCompanyDetailResponse(
        @Schema(description = "ID", example = "1")
        Long id,

        @Schema(description = "업체명", example = "삼성전자")
        String name,

        @Schema(description = "사업자등록번호", example = "123-45-67890")
        String businessNumber,

        @Schema(description = "구분", example = "장비")
        String type,

        @Schema(description = "구분", example = "EQUIPMENT")
        OutsourcingCompanyType typeCode,

        @Schema(description = "구분 설명", example = "도급업체")
        String typeDescription,

        @Schema(description = "대표자명", example = "홍길동")
        String ceoName,

        @Schema(description = "주소", example = "서울특별시 강남구")
        String address,

        @Schema(description = "상세주소", example = "역삼동 123-45")
        String detailAddress,

        @Schema(description = "전화번호", example = "02-1234-5678")
        String landlineNumber,

        @Schema(description = "개인 휴대폰", example = "010-1234-5678")
        String phoneNumber,

        @Schema(description = "이메일", example = "contact@samsung.com")
        String email,

        @Schema(description = "활성 여부", example = "true")
        boolean isActive,

        @Schema(description = "기본 공제 항목", example = "4대보험,소득세")
        String defaultDeductions,

        @Schema(description = "기본 공제 항목 코드", example = "FOUR_INSURANCE,INCOME_TAX")
        String defaultDeductionsCode,

        @Schema(description = "기본 공제 항목 설명", example = "4대 보험, 소득세")
        String defaultDeductionsDescription,

        @Schema(description = "은행명", example = "국민은행")
        String bankName,

        @Schema(description = "계좌번호", example = "123-456-7890")
        String accountNumber,

        @Schema(description = "예금주", example = "홍길동")
        String accountHolder,

        @Schema(description = "비고", example = "주요 거래처")
        String memo,

        @Schema(description = "담당자 목록")
        List<OutsourcingCompanyContactResponse> contacts,

        @Schema(description = "파일 목록")
        List<OutsourcingCompanyFileResponse> files
) {
    public static OutsourcingCompanyDetailResponse from(OutsourcingCompany company) {
        return new OutsourcingCompanyDetailResponse(
                company.getId(),
                company.getName(),
                company.getBusinessNumber(),
                company.getType().getLabel(),
                company.getType(),
                company.getTypeDescription(),
                company.getCeoName(),
                company.getAddress(),
                company.getDetailAddress(),
                company.getLandlineNumber(),
                company.getPhoneNumber(),
                company.getEmail(),
                company.isActive(),
                Arrays.stream(company.getDefaultDeductions().split(","))
                        .map(OutsourcingCompanyDefaultDeductionsType::safeLabelOf)
                        .collect(Collectors.joining(",")),
                company.getDefaultDeductions(),
                company.getDefaultDeductionsDescription(),
                company.getBankName(),
                company.getAccountNumber(),
                company.getAccountHolder(),
                company.getMemo(),
                company.getContacts().stream()
                        .map(OutsourcingCompanyContactResponse::from)
                        .toList(),
                company.getFiles().stream()
                        .map(OutsourcingCompanyFileResponse::from)
                        .toList()
        );
    }
}
