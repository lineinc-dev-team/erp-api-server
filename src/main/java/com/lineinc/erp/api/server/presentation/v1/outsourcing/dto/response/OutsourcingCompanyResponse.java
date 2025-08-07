package com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyDefaultDeductionsType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "외주업체 상세 응답")
public record OutsourcingCompanyResponse(
        @Schema(description = "ID")
        Long id,

        @Schema(description = "업체명")
        String name,

        @Schema(description = "사업자등록번호")
        String businessNumber,

        @Schema(description = "구분")
        String type,

        @Schema(description = "구분 코드")
        OutsourcingCompanyType typeCode,

        @Schema(description = "대표자명")
        String ceoName,

        @Schema(description = "주소")
        String address,

        @Schema(description = "상세주소")
        String detailAddress,

        @Schema(description = "전화번호")
        String landlineNumber,

        @Schema(description = "개인 휴대폰")
        String phoneNumber,

        @Schema(description = "이메일")
        String email,

        @Schema(description = "활성 여부")
        boolean isActive,

        @Schema(description = "기본 공제 항목")
        String defaultDeductions,

        @Schema(description = "기본 공제 항목 코드")
        String defaultDeductionsCode,

        @Schema(description = "기본 공제 항목 설명")
        String defaultDeductionsDescription,

        @Schema(description = "비고")
        String memo,

        @Schema(description = "등록일")
        OffsetDateTime createdAt,

        @Schema(description = "수정일")
        OffsetDateTime updatedAt,

        @Schema(description = "첨부파일 여부")
        boolean hasFile,

        @Schema(description = "담당자 목록")
        List<OutsourcingCompanyContactResponse> contacts,

        @Schema(description = "파일 목록")
        List<OutsourcingCompanyFileResponse> files
) {
    public static OutsourcingCompanyResponse from(OutsourcingCompany company) {
        return new OutsourcingCompanyResponse(
                company.getId(),
                company.getName(),
                company.getBusinessNumber(),
                company.getType().getLabel(),
                company.getType(),
                company.getCeoName(),
                company.getAddress(),
                company.getDetailAddress(),
                company.getLandlineNumber(),
                company.getPhoneNumber(),
                company.getEmail(),
                company.isActive(),
                Arrays.stream(company.getDefaultDeductions().split(","))
                        .map(OutsourcingCompanyDefaultDeductionsType::safeLabelOf)
                        .collect(Collectors.joining(", ")),
                company.getDefaultDeductions(),
                company.getDefaultDeductionsDescription(),
                company.getMemo(),
                company.getCreatedAt(),
                company.getUpdatedAt(),
                !company.getFiles().isEmpty(),
                company.getContacts().stream()
                        .map(OutsourcingCompanyContactResponse::from)
                        .toList(),
                company.getFiles().stream()
                        .map(OutsourcingCompanyFileResponse::from)
                        .toList()
        );
    }
}
