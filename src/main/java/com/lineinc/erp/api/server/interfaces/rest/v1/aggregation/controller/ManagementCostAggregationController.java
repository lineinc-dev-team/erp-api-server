package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.aggregation.managementcost.service.ManagementCostAggregationService;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ManagementCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/aggregation/management-cost")
@Tag(name = "집계")
public class ManagementCostAggregationController extends BaseController {

    private final ManagementCostAggregationService managementCostAggregationService;

    @GetMapping
    @Operation(summary = "관리비 집계 조회", description = "현장/공정/조회월 기준 관리비를 업체+항목별로 집계(전회, 금회)하여 반환합니다. 기타 항목은 항목설명 기준.")
    public ResponseEntity<SuccessResponse<ManagementCostAggregationResponse>> getManagementCostAggregation(
            @Valid final ManagementCostAggregationRequest request) {
        final ManagementCostAggregationResponse response = managementCostAggregationService.getManagementCostAggregation(request);
        return SuccessResponse.ok(response);
    }
}
