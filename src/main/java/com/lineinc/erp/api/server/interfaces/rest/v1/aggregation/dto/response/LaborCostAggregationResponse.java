package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import java.util.List;

import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.MaterialManagementItemResponse.BillingDetail;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 노무비 집계 응답
 */
@Schema(description = "노무비 집계 응답")
public record LaborCostAggregationResponse(
        @Schema(description = "노무비 집계 목록") List<LaborCostAggregationItem> items) {

    /**
     * 노무비 집계 항목 응답
     */
    @Schema(description = "노무비 집계 항목 응답")
    public record LaborCostAggregationItem(
            @Schema(description = "외주업체 요약 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany,
            @Schema(description = "인력 정보") LaborSimpleResponse labor,
            @Schema(description = "전회까지 청구내역") BillingDetail previousBilling,
            @Schema(description = "금회 청구내역") BillingDetail currentBilling) {
    }
}
