package com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 대시보드에서 사용할 간단한 현장 정보 응답.
 */
@Schema(description = "대시보드 현장 응답")
public record DashboardSiteResponse(
        @Schema(description = "현장 ID", example = "1") Long siteId,
        @Schema(description = "현장명", example = "서울 APT 신축공사") String siteName,
        @Schema(description = "공정 상태", example = "진행중") String status,
        @Schema(description = "공정 상태 코드", example = "IN_PROGRESS") SiteProcessStatus statusCode,
        @Schema(description = "사업 시작일") OffsetDateTime startedAt,
        @Schema(description = "사업 종료일") OffsetDateTime endedAt) {

    public static DashboardSiteResponse from(final Site site) {
        final SiteProcess process = resolvePrimaryProcess(site.getProcesses());
        final SiteProcessStatus statusCode = process != null ? process.getStatus() : null;
        final String statusLabel = statusCode != null ? statusCode.getLabel() : null;

        return new DashboardSiteResponse(
                site.getId(),
                site.getName(),
                statusLabel,
                statusCode,
                site.getStartedAt(),
                site.getEndedAt());
    }

    private static SiteProcess resolvePrimaryProcess(final List<SiteProcess> processes) {
        if (processes == null || processes.isEmpty()) {
            return null;
        }
        for (final SiteProcess process : processes) {
            if (!process.isDeleted()) {
                return process;
            }
        }
        return null;
    }
}
