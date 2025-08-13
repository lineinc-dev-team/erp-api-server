package com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.site;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.lineinc.erp.api.server.domain.site.enums.SiteType;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.sitecontract.CreateSiteContractRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.siteprocess.CreateSiteProcessRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "현장 등록 요청")
public record CreateSiteRequest(
        @NotBlank @Schema(description = "현장명", example = "서울 APT 신축공사") String name,

        @Schema(description = "주소", example = "서울시 강남구 역삼동") String address,

        @Schema(description = "상세 주소", example = "역삼로 123") String detailAddress,

        @Schema(description = "시", example = "서울시") String city,

        @Schema(description = "구", example = "강남구") String district,

        @NotNull @Schema(description = "현장 유형", example = "CONSTRUCTION") SiteType type,

        @Schema(description = "발주처 ID", example = "1") Long clientCompanyId,

        @Schema(description = "사업 시작일", example = "2024-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startedAt,

        @Schema(description = "사업 종료일", example = "2025-12-31") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endedAt,

        @Schema(description = "본사 담당자 ID", example = "5") Long userId,

        @Schema(description = "도급 금액", example = "100000000") Long contractAmount,

        @Schema(description = "비고") String memo,

        @Valid @Schema(description = "현장 공정 정보") CreateSiteProcessRequest process,

        @Valid @Schema(description = "현장 계약 목록") List<CreateSiteContractRequest> contracts) {
}
