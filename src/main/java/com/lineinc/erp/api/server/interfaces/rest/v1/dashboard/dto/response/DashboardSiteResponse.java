package com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 대시보드에서 사용할 간단한 현장 정보 응답.
 */
@Schema(description = "대시보드 현장 응답")
public record DashboardSiteResponse(
        @Schema(description = "현장 간단 정보") SiteResponse.SiteSimpleResponse site,
        @Schema(description = "공정 간단 정보") SiteProcessResponse.SiteProcessSimpleResponse siteProcess) {

    public static DashboardSiteResponse from(final Site site) {
        return new DashboardSiteResponse(
                SiteResponse.SiteSimpleResponse.from(site),
                SiteProcessResponse.SiteProcessSimpleResponse.from(site.getProcesses().get(0)));
    }
}
