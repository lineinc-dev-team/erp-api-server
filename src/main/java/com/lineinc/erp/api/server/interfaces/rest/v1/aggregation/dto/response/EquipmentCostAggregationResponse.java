package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import java.util.List;

import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.MaterialManagementItemResponse.BillingDetail;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 장비비 집계 응답
 */
@Schema(description = "장비비 집계 응답")
public record EquipmentCostAggregationResponse(
        @Schema(description = "장비비 집계 목록") List<EquipmentCostAggregationItem> items) {

    /**
     * 장비비 집계 항목 응답
     */
    @Schema(description = "장비비 집계 항목 응답")
    public record EquipmentCostAggregationItem(
            @Schema(description = "외주업체 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany,
            @Schema(description = "장비 규격", example = "25톤") String specification,
            @Schema(description = "전회까지 청구내역") BillingDetail previousBilling,
            @Schema(description = "금회 청구내역") BillingDetail currentBilling) {
    }
}
