package com.lineinc.erp.api.server.presentation.v1.site.dto.response.site;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.enums.SiteType;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.presentation.v1.client.dto.response.ClientCompanyResponse;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.siteprocess.SiteProcessResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.time.OffsetDateTime;

@Schema(description = "현장 정보 응답")
public record SiteResponse(
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

        @Schema(description = "현장 유형", example = "건축")
        String type,

        @Schema(description = "현장 유형 코드", example = "CONSTRUCTION")
        SiteType typeCode,

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

        @Schema(description = "첨부파일 존재 여부", example = "true")
        Boolean hasFile,

        @Valid
        @Schema(description = "공정 정보")
        SiteProcessResponse process,

        @Schema(description = "공정 소장 정보")
        UserResponse.UserSimpleResponse manager,

        @Schema(description = "발주처 정보")
        ClientCompanyResponse.ClientCompanySimpleResponse clientCompany
) {
    public static SiteResponse from(Site site) {
        return new SiteResponse(
                site.getId(),
                site.getName(),
                site.getAddress(),
                site.getDetailAddress(),
                site.getCity(),
                site.getDistrict(),
                site.getType().getLabel(),
                site.getType(),
                site.getStartedAt(),
                site.getEndedAt(),
                site.getContractAmount(),
                site.getMemo(),
                site.getCreatedAt(),
                site.getCreatedBy(),
                site.getUpdatedAt(),
                site.getContracts().stream().anyMatch(c -> c.getFiles() != null && !c.getFiles().isEmpty()),
                site.getProcesses() != null && !site.getProcesses().isEmpty()
                        ? SiteProcessResponse.from(site.getProcesses().get(0))
                        : null,
                site.getProcesses() != null && !site.getProcesses().isEmpty() && site.getProcesses().get(0).getManager() != null
                        ? UserResponse.UserSimpleResponse.from(site.getProcesses().get(0).getManager())
                        : null,
                site.getClientCompany() != null ? ClientCompanyResponse.ClientCompanySimpleResponse.from(site.getClientCompany()) : null
        );
    }

    @Schema(description = "간단한 현장 응답")
    public static record SiteSimpleResponse(
            @Schema(description = "현장 ID", example = "123")
            Long id,

            @Schema(description = "현장명", example = "서울 APT 신축공사")
            String name
    ) {
        public static SiteResponse.SiteSimpleResponse from(Site site) {
            return new SiteResponse.SiteSimpleResponse(site.getId(), site.getName());
        }
    }
}
