package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.aggregation.fuelaggregation.service.FuelAggregationDetailService;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.FuelAggregationDetailRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.FuelAggregationDetailResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 유류집계 상세 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/aggregation/fuel")
@RequiredArgsConstructor
@Tag(name = "집계")
public class FuelAggregationDetailController extends BaseController {

    private final FuelAggregationDetailService fuelAggregationDetailService;

    @GetMapping
    @Operation(summary = "유류비 집계 조회")
    public ResponseEntity<SuccessResponse<FuelAggregationDetailResponse>> getFuelAggregationDetail(
            @Valid final FuelAggregationDetailRequest request) {
        final FuelAggregationDetailResponse response = fuelAggregationDetailService.getFuelAggregationDetail(
                request.siteId(),
                request.siteProcessId(),
                request.yearMonth(),
                request.fuelType());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}
