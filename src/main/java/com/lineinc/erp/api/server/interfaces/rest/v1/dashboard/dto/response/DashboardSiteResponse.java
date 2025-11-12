package com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse.SiteSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 대시보드에서 사용할 간단한 현장 정보 응답.
 */
@Schema(description = "대시보드 현장 응답")
public record DashboardSiteResponse(
        @Schema(description = "현장 간단 응답") SiteSimpleResponse site) {

    public static DashboardSiteResponse from(final Site site) {
        return new DashboardSiteResponse(
                SiteSimpleResponse.from(site));
    }
}
