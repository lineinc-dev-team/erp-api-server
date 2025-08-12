package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 운전자 등록 요청")
public record OutsourcingCompanyContractDriverCreateRequest(
                @Schema(description = "운전자명", example = "김운전") String name,

                @Schema(description = "비고", example = "경력 15년") String memo,

                @Schema(description = "드라이버 서류 파일 목록") List<OutsourcingCompanyContractDriverFileCreateRequest> files) {
}
