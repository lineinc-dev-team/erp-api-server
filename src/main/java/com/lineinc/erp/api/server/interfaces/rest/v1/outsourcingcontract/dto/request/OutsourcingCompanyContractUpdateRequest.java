package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyTaxInvoiceConditionType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractCategoryType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "외주업체 계약 수정 요청")
public record OutsourcingCompanyContractUpdateRequest(
        @Schema(description = "계약 이름", example = "2025년 1월 계약") String contractName,
        @Schema(description = "현장 ID", example = "1") @NotNull Long siteId,
        @Schema(description = "공정 ID", example = "1") @NotNull Long siteProcessId,
        @Schema(description = "외주업체 ID", example = "1") @NotNull Long outsourcingCompanyId,
        @Schema(description = "계약 유형 설명", example = "설비 설치 계약") String typeDescription,
        @Schema(description = "계약 시작일", example = "2025-01-01") @NotNull LocalDate contractStartDate,
        @Schema(description = "계약 종료일", example = "2025-12-31") @NotNull LocalDate contractEndDate,
        @Schema(description = "계약 금액", example = "50000000") Long contractAmount,
        @Schema(description = "기본 공제 항목 (콤마로 구분된 문자열)", example = "FOUR_INSURANCES,MEAL_COST") String defaultDeductionsType,
        @Schema(description = "기본 공제 항목 설명", example = "4대 보험 및 연료비") String defaultDeductionsDescription,
        @Schema(description = "세금계산서 발행조건", example = "MONTH_END") OutsourcingCompanyTaxInvoiceConditionType taxInvoiceCondition,
        @Schema(description = "세금계산서 발행일(월)", example = "25") Integer taxInvoiceIssueDayOfMonth,
        @Schema(description = "계약 유형", example = "MONTHLY") OutsourcingCompanyContractCategoryType category,
        @Schema(description = "공종명", example = "콘크리트 타설") String workTypeName,
        @Schema(description = "계약 상태", example = "IN_PROGRESS") @NotNull OutsourcingCompanyContractStatus status,
        @Schema(description = "메모", example = "특이사항 없음") String memo,
        @Schema(description = "계약 담당자 목록") @Valid List<OutsourcingCompanyContractContactUpdateRequest> contacts,
        @Schema(description = "계약 첨부파일 목록") @Valid List<OutsourcingCompanyContractFileUpdateRequest> files,
        @Schema(description = "계약 인력 목록") @Valid List<OutsourcingCompanyContractWorkerUpdateRequest> workers,
        @Schema(description = "계약 장비 목록") @Valid List<OutsourcingCompanyContractEquipmentUpdateRequest> equipments,
        @Schema(description = "계약 운전자 목록") @Valid List<OutsourcingCompanyContractDriverUpdateRequest> drivers,
        @Schema(description = "계약 공사항목 목록") @Valid List<OutsourcingCompanyContractConstructionUpdateRequest> constructions,
        @Schema(description = "계약 공사항목 목록 V2") @Valid List<OutsourcingCompanyContractConstructionUpdateRequestV2> constructionsV2,
        @Schema(description = "변경 이력 리스트") @Valid List<OutsourcingCompanyContractUpdateRequest.ChangeHistoryRequest> changeHistories) {
    public record ChangeHistoryRequest(
            @Schema(description = "수정 이력 번호", example = "1") Long id,
            @Schema(description = "변경 사유 또는 비고", example = "계약 조건 변경에 따른 업데이트") String memo) {
    }
}
