package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lineinc.erp.api.server.domain.aggregation.materialcost.service.MaterialCostAggregationService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.MaterialCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 재료비 집계 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/aggregation/material-cost")
@RequiredArgsConstructor
@Tag(name = "집계")
public class MaterialCostAggregationController extends BaseController {

    private final MaterialCostAggregationService materialCostAggregationService;

    @GetMapping
    @Operation(summary = "재료비 조회")
    @RequireMenuPermission(menu = AppConstants.MENU_AGGREGATION_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<MaterialCostAggregationResponse>> getMaterialCostAggregation(
            @Valid final MaterialCostAggregationRequest request) {
        final MaterialCostAggregationResponse response = materialCostAggregationService
                .getMaterialCostAggregation(request.siteId(), request.siteProcessId(), request.yearMonth());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}
