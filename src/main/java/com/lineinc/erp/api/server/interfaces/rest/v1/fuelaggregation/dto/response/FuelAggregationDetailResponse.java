package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractDriverResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractEquipmentResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractListResponse.ContractSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유류집계 상세 정보 응답")
public record FuelAggregationDetailResponse(
        @Schema(description = "유류집계 ID", example = "1") Long id,
        @Schema(description = "유류업체 계약 간단 응답", example = "1") ContractSimpleResponse outsourcingCompanyContract,
        @Schema(description = "집계일자", example = "2025-07-28T10:30:00+09:00") OffsetDateTime date,
        @Schema(description = "날씨", example = "맑음") String weather,
        @Schema(description = "날씨 코드", example = "SUNNY") String weatherCode,

        @Schema(description = "휘발유 가격 (원)", example = "150000") Long gasolinePrice,
        @Schema(description = "경유 가격 (원)", example = "200000") Long dieselPrice,
        @Schema(description = "요소수 가격 (원)", example = "50000") Long ureaPrice,

        @Schema(description = "생성일", example = "2025-01-15T10:30:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2025-01-16T14:20:00+09:00") OffsetDateTime updatedAt,
        @Schema(description = "유류정보 목록") List<FuelInfoDetailResponse> fuelInfos,
        @Schema(description = "현장 요약 정보") SiteResponse.SiteSimpleResponse site,
        @Schema(description = "공정 요약 정보") SiteProcessResponse.SiteProcessSimpleResponse process) {

    public static FuelAggregationDetailResponse from(final FuelAggregation entity,
            final DailyReportRepository dailyReportRepository) {
        // 출역일보에서 날씨 정보 가져오기
        String weatherLabel = null;
        String weatherCode = null;

        if (entity.getSite() != null && entity.getSiteProcess() != null && entity.getDate() != null) {
            final var dailyReport = dailyReportRepository.findBySiteAndSiteProcessAndReportDate(
                    entity.getSite(), entity.getSiteProcess(),
                    entity.getDate());

            if (dailyReport.isPresent() && dailyReport.get().getWeather() != null) {
                weatherLabel = dailyReport.get().getWeather().getLabel();
                weatherCode = dailyReport.get().getWeather().name();
            }
        }

        return new FuelAggregationDetailResponse(
                entity.getId(),
                entity.getOutsourcingCompanyContract() != null
                        ? ContractSimpleResponse.from(entity.getOutsourcingCompanyContract())
                        : null,
                entity.getDate(),
                weatherLabel,
                weatherCode,
                entity.getGasolinePrice(),
                entity.getDieselPrice(),
                entity.getUreaPrice(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getFuelInfos().stream()
                        .map(FuelInfoDetailResponse::from)
                        .sorted(Comparator.comparing(FuelInfoDetailResponse::id))
                        .toList(),
                SiteResponse.SiteSimpleResponse.from(entity.getSite()),
                SiteProcessResponse.SiteProcessSimpleResponse.from(entity.getSiteProcess()));
    }

    @Schema(description = "유류정보 상세 응답")
    public record FuelInfoDetailResponse(
            @Schema(description = "유류정보 ID", example = "1") Long id,
            @Schema(description = "기사 정보") ContractDriverResponse.ContractDriverSimpleResponse driver,
            @Schema(description = "장비 정보") ContractEquipmentResponse.ContractEquipmentSimpleResponse equipment,
            @Schema(description = "유종", example = "경유") String fuelType,
            @Schema(description = "유종 코드", example = "DIESEL") String fuelTypeCode,
            @Schema(description = "주유량 (리터)", example = "50") Long fuelAmount,
            @Schema(description = "구분", example = "장비") String categoryType,
            @Schema(description = "구분 코드", example = "EQUIPMENT") String categoryTypeCode,
            @Schema(description = "등록일", example = "2025-01-15T10:30:00+09:00") OffsetDateTime createdAt,
            @Schema(description = "수정일", example = "2025-01-16T14:20:00+09:00") OffsetDateTime updatedAt,
            @Schema(description = "비고", example = "오전 주유") String memo,
            @Schema(description = "사진 URL", example = "https://example.com/photo.jpg") String fileUrl,
            @Schema(description = "사진 원본 파일명", example = "photo.jpg") String originalFileName,
            @Schema(description = "업체 요약 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany) {

        public static FuelInfoDetailResponse from(final FuelInfo entity) {
            return new FuelInfoDetailResponse(
                    entity.getId(),
                    entity.getDriver() != null
                            ? ContractDriverResponse.ContractDriverSimpleResponse.from(entity.getDriver())
                            : null,
                    entity.getEquipment() != null
                            ? ContractEquipmentResponse.ContractEquipmentSimpleResponse.from(entity.getEquipment())
                            : null,
                    entity.getFuelType() != null ? entity.getFuelType().getLabel() : null,
                    entity.getFuelType() != null ? entity.getFuelType().name() : null,
                    entity.getFuelAmount(),
                    entity.getCategoryType() != null ? entity.getCategoryType().getLabel() : null,
                    entity.getCategoryType() != null ? entity.getCategoryType().name() : null,
                    entity.getCreatedAt(),
                    entity.getUpdatedAt(),
                    entity.getMemo(),
                    entity.getFileUrl(),
                    entity.getOriginalFileName(),
                    entity.getOutsourcingCompany() != null
                            ? CompanyResponse.CompanySimpleResponse.from(entity.getOutsourcingCompany())
                            : null);
        }
    }
}
