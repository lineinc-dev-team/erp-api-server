package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractDefaultDeductionsType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 목록 응답")
public record ContractListResponse(
        @Schema(description = "계약 ID") Long id,

        @Schema(description = "현장명") String siteName,

        @Schema(description = "공정명") String processName,

        @Schema(description = "외주업체명") String companyName,

        @Schema(description = "사업자등록번호") String businessNumber,

        @Schema(description = "계약 구분") String contractType,

        @Schema(description = "계약 구분 설명") String typeDescription,

        @Schema(description = "계약 상태") String contractStatus,

        @Schema(description = "계약 유형 카테고리") String categoryType,

        @Schema(description = "외주금액") Long contractAmount,

        @Schema(description = "담당자 목록") List<CompanyContactResponse> contacts,

        @Schema(description = "공제항목") String defaultDeductions,

        @Schema(description = "세금계산서 발행조건") String taxInvoiceCondition,

        @Schema(description = "세금계산서 발행일") Integer taxInvoiceIssueDayOfMonth,

        @Schema(description = "메모") String memo,

        @Schema(description = "계약 시작일") OffsetDateTime contractStartDate,

        @Schema(description = "계약 종료일") OffsetDateTime contractEndDate,

        @Schema(description = "등록일") OffsetDateTime createdAt,

        @Schema(description = "수정일") OffsetDateTime updatedAt,

        @Schema(description = "파일 첨부 여부") Boolean hasFile) {

    public static ContractListResponse from(OutsourcingCompanyContract contract) {
        // 담당자 목록 생성
        List<CompanyContactResponse> contacts = contract.getContacts() != null
                && !contract.getContacts().isEmpty()
                        ? contract.getContacts().stream()
                                .map(CompanyContactResponse::from)
                                .toList()
                        : List.of();

        // 공제항목을 콤마로 구분하여 각각의 label로 변환
        String defaultDeductionsLabel = null;
        if (contract.getDefaultDeductions() != null && !contract.getDefaultDeductions().trim().isEmpty()) {
            defaultDeductionsLabel = java.util.Arrays.stream(contract.getDefaultDeductions().split(","))
                    .map(String::trim)
                    .map(OutsourcingCompanyContractDefaultDeductionsType::safeLabelOf)
                    .collect(java.util.stream.Collectors.joining(","));
        }

        // 파일 첨부 여부 확인
        Boolean hasFile = contract.getFiles() != null && !contract.getFiles().isEmpty();

        return new ContractListResponse(
                contract.getId(),
                contract.getSite() != null ? contract.getSite().getName() : null,
                contract.getSiteProcess() != null ? contract.getSiteProcess().getName() : null,
                contract.getOutsourcingCompany() != null ? contract.getOutsourcingCompany().getName()
                        : null,
                contract.getOutsourcingCompany().getBusinessNumber(),
                contract.getType() != null ? contract.getType().getLabel() : null,
                contract.getTypeDescription(),
                contract.getStatus() != null ? contract.getStatus().getLabel() : null,
                contract.getCategory() != null ? contract.getCategory().getLabel() : null,
                contract.getContractAmount(),
                contacts,
                defaultDeductionsLabel,
                contract.getTaxInvoiceCondition() != null ? contract.getTaxInvoiceCondition().getLabel()
                        : null,
                contract.getTaxInvoiceIssueDayOfMonth(),
                contract.getMemo(),
                contract.getContractStartDate(),
                contract.getContractEndDate(),
                contract.getCreatedAt(),
                contract.getUpdatedAt(),
                hasFile);
    }
}
