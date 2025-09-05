package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportStatus;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.WeatherType;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse.SiteSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse.SiteProcessSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(description = "출역일보 상세 조회 응답")
public record DailyReportDetailResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "현장 정보") SiteSimpleResponse site,
        @Schema(description = "공정 정보") SiteProcessSimpleResponse siteProcess,
        @Schema(description = "출역일보 일자", example = "2024-01-15T00:00:00+09:00") OffsetDateTime reportDate,
        @Schema(description = "날씨", example = "맑음") String weather,
        @Schema(description = "날씨 코드", example = "SUNNY") WeatherType weatherCode,
        @Schema(description = "출역일보 상태", example = "PENDING") DailyReportStatus status,
        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt,
        @Schema(description = "마감일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime completedAt) {
    public static DailyReportDetailResponse from(DailyReport dailyReport) {
        return new DailyReportDetailResponse(
                dailyReport.getId(),
                dailyReport.getSite() != null ? SiteSimpleResponse.from(dailyReport.getSite()) : null,
                dailyReport.getSiteProcess() != null ? SiteProcessSimpleResponse.from(dailyReport.getSiteProcess())
                        : null,
                dailyReport.getReportDate(),
                dailyReport.getWeather() != null ? dailyReport.getWeather().getLabel() : null,
                dailyReport.getWeather(),
                dailyReport.getStatus(),
                dailyReport.getCreatedAt(),
                dailyReport.getUpdatedAt(),
                dailyReport.getCompletedAt());
    }
}
