package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.site.SiteResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.siteprocess.SiteProcessResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유류집계 정보 응답")
public record FuelAggregationListResponse(
        @Schema(description = "유류집계 ID", example = "1") Long id,

        @Schema(description = "일자", example = "2025-07-28T10:30:00+09:00") OffsetDateTime date,

        @Schema(description = "생성일", example = "2025-01-15T10:30:00+09:00") OffsetDateTime createdAt,

        @Schema(description = "수정일", example = "2025-01-16T14:20:00+09:00") OffsetDateTime updatedAt,

        @Schema(description = "현장 요약 정보") SiteResponse.SiteSimpleResponse site,

        @Schema(description = "공정 요약 정보") SiteProcessResponse.SiteProcessSimpleResponse process,

        @Schema(description = "유류정보") FuelInfoListResponse fuelInfo) {

    public static FuelAggregationListResponse from(FuelAggregation entity, FuelInfo fuelInfo) {
        return new FuelAggregationListResponse(
                entity.getId(),
                entity.getDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                SiteResponse.SiteSimpleResponse.from(entity.getSite()),
                SiteProcessResponse.SiteProcessSimpleResponse.from(entity.getSiteProcess()),
                FuelInfoListResponse.from(fuelInfo));
    }

    @Schema(description = "유류정보 응답")
    public record FuelInfoListResponse(
            @Schema(description = "유류정보 ID", example = "1") Long id,

            @Schema(description = "기사명", example = "김철수") String driverName,

            @Schema(description = "차량번호", example = "12가3456") String vehicleNumber,

            @Schema(description = "규격", example = "굴삭기") String specification,

            @Schema(description = "유종", example = "경유") String fuelType,

            @Schema(description = "유종 코드", example = "DIESEL") String fuelTypeCode,

            @Schema(description = "주유량 (리터)", example = "50") Long fuelAmount,

            @Schema(description = "등록일", example = "2025-01-15T10:30:00+09:00") OffsetDateTime createdAt,

            @Schema(description = "수정일", example = "2025-01-16T14:20:00+09:00") OffsetDateTime updatedAt,

            @Schema(description = "비고", example = "오전 주유") String memo,

            @Schema(description = "업체 요약 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany) {

        public static FuelInfoListResponse from(FuelInfo entity) {
            return new FuelInfoListResponse(
                    entity.getId(),
                    entity.getDriver() != null ? entity.getDriver().getName() : null,
                    entity.getEquipment() != null ? entity.getEquipment().getVehicleNumber() : null,
                    entity.getEquipment() != null ? entity.getEquipment().getSpecification() : null,
                    entity.getFuelType() != null ? entity.getFuelType().getLabel() : null,
                    entity.getFuelType() != null ? entity.getFuelType().name() : null,
                    entity.getFuelAmount(),
                    entity.getCreatedAt(),
                    entity.getUpdatedAt(),
                    entity.getMemo(),
                    entity.getOutsourcingCompany() != null
                            ? CompanyResponse.CompanySimpleResponse.from(entity.getOutsourcingCompany())
                            : null);
        }
    }
}
