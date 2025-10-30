package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import java.util.List;

import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;

public record ManagementCostAggregationResponse(
        List<ManagementCostAggregationItem> items) {

    public record ManagementCostAggregationItem(
            CompanyResponse.CompanySimpleResponse outsourcingCompany,
            String itemType,
            ManagementCostItemType itemTypeCode,
            String itemTypeDescription,
            BillingDetail previousBilling,
            BillingDetail currentBilling) {
    }

    public record BillingDetail(
            long supplyPrice,
            long vat,
            long deduction,
            long total) {
    }
}
