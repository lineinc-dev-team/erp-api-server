package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.aggregation.laborcost.service.LaborCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.outsourcinglaborcost.service.OutsourcingLaborCostAggregationService;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.LaborCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.OutsourcingLaborCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.LaborCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.OutsourcingLaborCostAggregationResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 노무비 집계 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/aggregation")
@RequiredArgsConstructor
@Tag(name = "노무비 집계", description = "노무비 집계 API")
public class LaborCostAggregationController extends BaseController {

    private final LaborCostAggregationService laborCostAggregationService;
    private final OutsourcingLaborCostAggregationService outsourcingLaborCostAggregationService;

    @GetMapping("/labor-cost")
    @Operation(summary = "노무비 집계 조회")
    public ResponseEntity<SuccessResponse<LaborCostAggregationResponse>> getLaborCostAggregation(
            @Valid final LaborCostAggregationRequest request) {
        final LaborCostAggregationResponse response = laborCostAggregationService.getLaborCostAggregation(
                request.siteId(), request.siteProcessId(), request.yearMonth(), request.laborType());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @GetMapping("/outsourcing-labor-cost")
    @Operation(summary = "용역업체별 노무비 집계 조회")
    public ResponseEntity<SuccessResponse<OutsourcingLaborCostAggregationResponse>> getOutsourcingLaborCostAggregation(
            @Valid final OutsourcingLaborCostAggregationRequest request) {
        final OutsourcingLaborCostAggregationResponse response = outsourcingLaborCostAggregationService
                .getOutsourcingLaborCostAggregation(request.siteId(), request.siteProcessId(), request.yearMonth());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}
