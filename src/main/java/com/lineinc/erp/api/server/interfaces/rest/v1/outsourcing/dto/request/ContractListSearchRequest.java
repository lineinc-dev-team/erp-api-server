package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;

import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractStatus;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractType;

import io.swagger.v3.oas.annotations.media.Schema;

@ParameterObject
@Schema(description = "외주계약 리스트 검색 조건")
public record ContractListSearchRequest(
        @Schema(description = "현장명 (검색어)", example = "서울역 신축공사") String siteName,

        @Schema(description = "공정명 (검색어)", example = "토목공사") String processName,

        @Schema(description = "외주업체명 (검색어)", example = "한국건설") String companyName,

        @Schema(description = "계약 구분", example = "SERVICE") OutsourcingCompanyContractType contractType,

        @Schema(description = "계약 상태", example = "IN_PROGRESS") OutsourcingCompanyContractStatus contractStatus,

        @Schema(description = "계약 시작일 (시작)", example = "2024-01-01") LocalDate contractStartDate,

        @Schema(description = "계약 종료일 (시작)", example = "2024-12-31") LocalDate contractEndDate,

        @Schema(description = "담당자명 (검색어)", example = "홍길동") String contactName) {
}
