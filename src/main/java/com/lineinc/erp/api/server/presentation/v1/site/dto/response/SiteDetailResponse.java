package com.lineinc.erp.api.server.presentation.v1.site.dto.response;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.response.UserResponse.UserSimpleResponse;
import com.lineinc.erp.api.server.presentation.v1.client.dto.response.ClientCompanyResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.lineinc.erp.api.server.presentation.v1.site.dto.response.SiteContractResponse;

@Schema(description = "현장 정보 응답")
public record SiteDetailResponse(
        @Schema(description = "현장 ID", example = "1")
        Long id,

        @Schema(description = "현장명", example = "서울 APT 신축공사")
        String name,

        @Schema(description = "주소", example = "서울시 강남구 역삼동")
        String address,

        @Schema(description = "상세 주소", example = "역삼로 123")
        String detailAddress,

        @Schema(description = "시", example = "서울시")
        String city,

        @Schema(description = "구", example = "강남구")
        String district,

        @Schema(description = "현장 유형", example = "CONSTRUCTION")
        String type,

        @Schema(description = "사업 시작일", example = "2024-01-01T00:00:00+09:00")
        OffsetDateTime startedAt,

        @Schema(description = "사업 종료일", example = "2025-12-31T00:00:00+09:00")
        OffsetDateTime endedAt,

        @Schema(description = "도급금액", example = "100000000")
        Long contractAmount,

        @Schema(description = "비고", example = "비고 내용")
        String memo,

        @Schema(description = "등록일")
        OffsetDateTime createdAt,

        @Schema(description = "등록자", example = "홍길동")
        String createdBy,

        @Schema(description = "수정일")
        OffsetDateTime updatedAt,

        @Valid
        @Schema(description = "공정 정보")
        SiteProcessResponse process,

        @Schema(description = "발주처 정보")
        ClientCompanyResponse.ClientCompanySimpleResponse clientCompany,

        @Schema(description = "본사 담당자 정보")
        UserSimpleResponse user,

        @Schema(description = "계약 정보 목록")
        List<SiteContractResponse> contracts
) {
    public static SiteDetailResponse from(Site site) {
        List<SiteContractResponse> contractResponses = site.getContracts().stream()
                .map(SiteContractResponse::from)
                .collect(Collectors.toList());

        return new SiteDetailResponse(
                site.getId(),
                site.getName(),
                site.getAddress(),
                site.getDetailAddress(),
                site.getCity(),
                site.getDistrict(),
                site.getType().getLabel(),
                site.getStartedAt(),
                site.getEndedAt(),
                site.getContractAmount(),
                site.getMemo(),
                site.getCreatedAt(),
                site.getCreatedBy(),
                site.getUpdatedAt(),
                site.getProcesses() != null && !site.getProcesses().isEmpty()
                        ? SiteProcessResponse.from(site.getProcesses().get(0))
                        : null,
                site.getClientCompany() != null ? ClientCompanyResponse.ClientCompanySimpleResponse.from(site.getClientCompany()) : null,
                site.getUser() != null ? UserSimpleResponse.from(site.getUser()) : null,
                contractResponses
        );
    }
}
