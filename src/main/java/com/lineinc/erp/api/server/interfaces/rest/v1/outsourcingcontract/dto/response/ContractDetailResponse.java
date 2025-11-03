package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 상세 응답")
public record ContractDetailResponse(
        @Schema(description = "계약 ID", example = "1") Long id,
        @Schema(description = "계약 이름", example = "2025년 1월 계약") String name,
        @Schema(description = "계약 구분", example = "토목공사") String type,
        @Schema(description = "계약 구분 코드", example = "CIVIL_ENGINEERING") String typeCode,
        @Schema(description = "계약 구분 설명", example = "토목공사 계약") String typeDescription,
        @Schema(description = "계약 시작일") OffsetDateTime contractStartDate,
        @Schema(description = "계약 종료일") OffsetDateTime contractEndDate,
        @Schema(description = "계약 금액", example = "50000000") Long contractAmount,
        @Schema(description = "공제 항목", example = "식대,교통비") String defaultDeductions,
        @Schema(description = "공제 항목 코드", example = "FOUR_INSURANCES,FUEL_COST") String defaultDeductionsCode,
        @Schema(description = "공제 항목 설명", example = "일일 식대 1만원, 교통비 5천원") String defaultDeductionsDescription,
        @Schema(description = "세금계산서 발행조건", example = "월말일괄") String taxInvoiceCondition,
        @Schema(description = "세금계산서 발행조건 코드", example = "MONTHLY") String taxInvoiceConditionCode,
        @Schema(description = "세금계산서 발행일", example = "25") Integer taxInvoiceIssueDayOfMonth,
        @Schema(description = "계약 유형 카테고리", example = "월대") String category,
        @Schema(description = "계약 유형 카테고리 코드", example = "CONSTRUCTION") String categoryCode,
        @Schema(description = "공종명", example = "콘크리트 타설") String workTypeName,
        @Schema(description = "계약 상태", example = "진행중") String status,
        @Schema(description = "계약 상태 코드", example = "IN_PROGRESS") String statusCode,
        @Schema(description = "메모", example = "특별 주의사항") String memo,
        @Schema(description = "생성일시") OffsetDateTime createdAt,
        @Schema(description = "수정일시") OffsetDateTime updatedAt,
        @Schema(description = "현장 정보") SiteResponse.SiteSimpleResponse site,
        @Schema(description = "공정 정보") SiteProcessResponse.SiteProcessSimpleResponse siteProcess,
        @Schema(description = "외주업체 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany,
        @Schema(description = "계약 담당자 목록") List<ContractContactResponse> contacts,
        @Schema(description = "계약 첨부파일 목록") List<ContractFileResponse> files) {

    public static ContractDetailResponse from(final OutsourcingCompanyContract contract) {
        return new ContractDetailResponse(
                contract.getId(),
                contract.getName(),
                contract.getType() != null ? contract.getType().getLabel() : null,
                contract.getType() != null ? contract.getType().name() : null,
                contract.getTypeDescription(),
                contract.getContractStartDate(),
                contract.getContractEndDate(),
                contract.getContractAmount(),
                contract.getDefaultDeductions() != null && !contract.getDefaultDeductions().trim().isEmpty()
                        ? Arrays.stream(contract.getDefaultDeductions().split(","))
                                .map(String::trim)
                                .map(com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractDefaultDeductionsType::safeLabelOf)
                                .collect(Collectors.joining(","))
                        : null,
                contract.getDefaultDeductions() != null ? contract.getDefaultDeductions() : null,
                contract.getDefaultDeductionsDescription(),
                contract.getTaxInvoiceCondition() != null ? contract.getTaxInvoiceCondition().getLabel() : null,
                contract.getTaxInvoiceCondition() != null ? contract.getTaxInvoiceCondition().name() : null,
                contract.getTaxInvoiceIssueDayOfMonth(),
                contract.getCategory() != null ? contract.getCategory().getLabel() : null,
                contract.getCategory() != null ? contract.getCategory().name() : null,
                contract.getWorkTypeName(),
                contract.getStatus() != null ? contract.getStatus().getLabel() : null,
                contract.getStatus() != null ? contract.getStatus().name() : null,
                contract.getMemo(),
                contract.getCreatedAt(),
                contract.getUpdatedAt(),
                contract.getSite() != null ? SiteResponse.SiteSimpleResponse.from(contract.getSite()) : null,
                contract.getSiteProcess() != null
                        ? SiteProcessResponse.SiteProcessSimpleResponse.from(contract.getSiteProcess())
                        : null,
                contract.getOutsourcingCompany() != null
                        ? CompanyResponse.CompanySimpleResponse.from(contract.getOutsourcingCompany())
                        : null,
                contract.getContacts() != null ? contract.getContacts().stream()
                        .map(ContractContactResponse::from)
                        .sorted(Comparator.comparing(ContractContactResponse::id))
                        .toList() : List.of(),
                contract.getFiles() != null ? contract.getFiles().stream()
                        .map(ContractFileResponse::from)
                        .sorted(Comparator.comparing(ContractFileResponse::id))
                        .toList() : List.of());
    }
}
