package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyDefaultDeductionsType;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 목록 응답")
public record CompanyResponse(
        @Schema(description = "ID", example = "1") Long id,

        @Schema(description = "업체명", example = "삼성전자") String name,

        @Schema(description = "사업자등록번호", example = "123-45-67890") String businessNumber,

        @Schema(description = "구분", example = "용역") String type,

        @Schema(description = "구분 코드", example = "SERVICE") OutsourcingCompanyType typeCode,

        @Schema(description = "대표자명", example = "홍길동") String ceoName,

        @Schema(description = "주소", example = "서울특별시 강남구") String address,

        @Schema(description = "상세주소", example = "역삼동 123-45") String detailAddress,

        @Schema(description = "전화번호", example = "02-1234-5678") String landlineNumber,

        @Schema(description = "개인 휴대폰", example = "010-1234-5678") String phoneNumber,

        @Schema(description = "이메일", example = "contact@samsung.com") String email,

        @Schema(description = "활성 여부", example = "true") boolean isActive,

        @Schema(description = "기본 공제 항목", example = "4대보험,소득세") String defaultDeductions,

        @Schema(description = "기본 공제 항목 코드", example = "FOUR_INSURANCE,INCOME_TAX") String defaultDeductionsCode,

        @Schema(description = "기본 공제 항목 설명", example = "4대 보험, 소득세") String defaultDeductionsDescription,

        @Schema(description = "비고", example = "주요 거래처") String memo,

        @Schema(description = "등록일", example = "2025-08-07T15:00:00+09:00") OffsetDateTime createdAt,

        @Schema(description = "수정일", example = "2025-08-07T17:00:00+09:00") OffsetDateTime updatedAt,

        @Schema(description = "첨부파일 여부", example = "true") boolean hasFile,

        @Schema(description = "담당자 목록") List<CompanyContactResponse> contacts

) {
    public static CompanyResponse from(final OutsourcingCompany company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getBusinessNumber(),
                company.getType() != null ? company.getType().getLabel() : null,
                company.getType(),
                company.getCeoName(),
                company.getAddress(),
                company.getDetailAddress(),
                company.getLandlineNumber(),
                company.getPhoneNumber(),
                company.getEmail(),
                company.isActive(),
                company.getDefaultDeductions() != null && !company.getDefaultDeductions().isEmpty()
                        ? Arrays.stream(company.getDefaultDeductions().split(","))
                                .map(OutsourcingCompanyDefaultDeductionsType::safeLabelOf)
                                .collect(Collectors.joining(","))
                        : null,
                company.getDefaultDeductions(),
                company.getDefaultDeductionsDescription(),
                company.getMemo(),
                company.getCreatedAt(),
                company.getUpdatedAt(),
                company.getFiles().stream()
                        .anyMatch(file -> file.getFileUrl() != null && !file.getFileUrl().isBlank()),
                company.getContacts().stream()
                        .filter(contact -> contact.getIsMain())
                        .map(CompanyContactResponse::from)
                        .toList());

    }

    @Schema(description = "간단한 외주업체 응답")
    public static record CompanySimpleResponse(
            @Schema(description = "외주업체 ID", example = "123") Long id,
            @Schema(description = "외주업체 이름", example = "삼성건설") String name,
            @Schema(description = "외주업체 사업자등록번호", example = "123-45-67890") String businessNumber,
            @Schema(description = "외주업체 구분", example = "외주") String type,
            @Schema(description = "외주업체 구분 코드", example = "CONSTRUCTION") OutsourcingCompanyType typeCode,
            @Schema(description = "대표자명", example = "홍길동") String ceoName,
            @Schema(description = "은행명", example = "신한은행") String bankName,
            @Schema(description = "계좌번호", example = "123-456-789012") String accountNumber,
            @Schema(description = "예금주", example = "홍길동") String accountHolder,
            @Schema(description = "삭제 여부", example = "false") Boolean deleted) {
        public static CompanyResponse.CompanySimpleResponse from(final OutsourcingCompany company) {
            return new CompanyResponse.CompanySimpleResponse(
                    company.getId(),
                    company.getName(),
                    company.getBusinessNumber(),
                    company.getType() != null ? company.getType().getLabel() : null,
                    company.getType(),
                    company.getCeoName(),
                    company.getBankName(),
                    company.getAccountNumber(),
                    company.getAccountHolder(),
                    company.isDeleted());
        }
    }

}
