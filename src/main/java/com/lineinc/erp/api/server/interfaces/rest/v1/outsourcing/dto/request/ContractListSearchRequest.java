package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import java.time.OffsetDateTime;

import org.springdoc.core.annotations.ParameterObject;

import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractStatus;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractType;

import io.swagger.v3.oas.annotations.media.Schema;

@ParameterObject
@Schema(description = "외주계약 리스트 검색 조건")
public record ContractListSearchRequest(
        @Schema(description = "현장명 (검색어)") String siteName,

        @Schema(description = "공정명 (검색어)") String processName,

        @Schema(description = "외주업체명 (검색어)") String companyName,

        @Schema(description = "계약 구분") OutsourcingCompanyContractType contractType,

        @Schema(description = "계약 상태") OutsourcingCompanyContractStatus contractStatus,

        @Schema(description = "계약 시작일 (시작)") OffsetDateTime contractStartDate,

        @Schema(description = "계약 종료일 (시작)") OffsetDateTime contractEndDate,

        @Schema(description = "담당자명 (검색어)") String contactName) {
}
