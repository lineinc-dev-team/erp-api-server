package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 운전자 수정 요청")
public record OutsourcingCompanyContractDriverUpdateRequest(
        @Schema(description = "운전자 ID", example = "1") Long id,

        @Schema(description = "운전자명", example = "김운전") String name,

        @Schema(description = "비고", example = "경력 15년") String memo,

        @Schema(description = "드라이버 서류 파일 목록") List<OutsourcingCompanyContractDriverFileUpdateRequest> files) {
}
