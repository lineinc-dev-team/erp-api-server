package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementV2;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재수불부 V2 응답")
public record SteelManagementV2Response(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "현장 요약 정보") SiteResponse.SiteSimpleResponse site,
        @Schema(description = "공정 요약 정보") SiteProcessResponse.SiteProcessSimpleResponse process,
        @Schema(description = "생성일시") OffsetDateTime createdAt,
        @Schema(description = "수정일시") OffsetDateTime updatedAt,

        // 입고 집계
        @Schema(description = "입고-자사자재 총무게", example = "1000.0") Double incomingOwnMaterialTotalWeight,
        @Schema(description = "입고-자사자재 금액", example = "5000000") Long incomingOwnMaterialAmount,
        @Schema(description = "입고-구매 총무게", example = "2000.0") Double incomingPurchaseTotalWeight,
        @Schema(description = "입고-구매 금액", example = "10000000") Long incomingPurchaseAmount,
        @Schema(description = "입고-임대 총무게", example = "500.0") Double incomingRentalTotalWeight,
        @Schema(description = "입고-임대 금액", example = "3000000") Long incomingRentalAmount,

        // 출고 집계
        @Schema(description = "출고-자사자재 총무게", example = "800.0") Double outgoingOwnMaterialTotalWeight,
        @Schema(description = "출고-자사자재 금액", example = "4000000") Long outgoingOwnMaterialAmount,
        @Schema(description = "출고-구매 총무게", example = "1500.0") Double outgoingPurchaseTotalWeight,
        @Schema(description = "출고-구매 금액", example = "7500000") Long outgoingPurchaseAmount,
        @Schema(description = "출고-임대 총무게", example = "300.0") Double outgoingRentalTotalWeight,
        @Schema(description = "출고-임대 금액", example = "2000000") Long outgoingRentalAmount,

        // 사장 집계
        @Schema(description = "사장 총무게", example = "1200.0") Double onSiteStockTotalWeight,

        // 고철 집계
        @Schema(description = "고철 총무게", example = "100.0") Double scrapTotalWeight,
        @Schema(description = "고철 금액", example = "500000") Long scrapAmount,

        // 최종 집계
        @Schema(description = "총 금액(투입비)", example = "15000000") Long totalInvestmentAmount,
        @Schema(description = "현장보류수량", example = "3000000") Long onSiteRemainingAmount) {

    public static SteelManagementV2Response from(final SteelManagementV2 entity) {
        return new SteelManagementV2Response(
                entity.getId(),
                SiteResponse.SiteSimpleResponse.from(entity.getSite()),
                SiteProcessResponse.SiteProcessSimpleResponse.from(entity.getSiteProcess()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getIncomingOwnMaterialTotalWeight(),
                entity.getIncomingOwnMaterialAmount(),
                entity.getIncomingPurchaseTotalWeight(),
                entity.getIncomingPurchaseAmount(),
                entity.getIncomingRentalTotalWeight(),
                entity.getIncomingRentalAmount(),
                entity.getOutgoingOwnMaterialTotalWeight(),
                entity.getOutgoingOwnMaterialAmount(),
                entity.getOutgoingPurchaseTotalWeight(),
                entity.getOutgoingPurchaseAmount(),
                entity.getOutgoingRentalTotalWeight(),
                entity.getOutgoingRentalAmount(),
                entity.getOnSiteStockTotalWeight(),
                entity.getScrapTotalWeight(),
                entity.getScrapAmount(),
                entity.getTotalInvestmentAmount(),
                entity.getOnSiteRemainingAmount());
    }
}
