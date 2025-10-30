package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.aggregation.equipmentcost.service.EquipmentCostAggregationService;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.EquipmentCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentCostAggregationResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 장비비 집계 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/aggregation/equipment-cost")
@RequiredArgsConstructor
@Tag(name = "집계")
public class EquipmentCostAggregationController extends BaseController {

    private final EquipmentCostAggregationService equipmentCostAggregationService;

    @GetMapping
    @Operation(summary = "장비비 조회")
    public ResponseEntity<SuccessResponse<EquipmentCostAggregationResponse>> getEquipmentCostAggregation(
            @Valid final EquipmentCostAggregationRequest request) {
        final EquipmentCostAggregationResponse response = equipmentCostAggregationService
                .getEquipmentCostAggregation(request.siteId(), request.siteProcessId(), request.yearMonth());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}
