package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.aggregation.headquarter.service.HeadquarterAggregationService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.HeadquarterAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.HeadquarterAggregationResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 본사 집계 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/aggregation/headquarter")
@Tag(name = "집계")
public class HeadquarterAggregationController extends BaseController {

    private final HeadquarterAggregationService headquarterAggregationService;

    /**
     * 본사 집계 조회
     */
    @GetMapping
    @Operation(summary = "본사 집계 조회")
    @RequireMenuPermission(menu = AppConstants.MENU_AGGREGATION_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<HeadquarterAggregationResponse>> getHeadquarterAggregation(
            @Valid final HeadquarterAggregationRequest request) {
        final HeadquarterAggregationResponse response = headquarterAggregationService.getHeadquarterAggregation(
                request.siteId(),
                request.siteProcessId(),
                request.yearMonth());
        return SuccessResponse.ok(response);
    }
}
