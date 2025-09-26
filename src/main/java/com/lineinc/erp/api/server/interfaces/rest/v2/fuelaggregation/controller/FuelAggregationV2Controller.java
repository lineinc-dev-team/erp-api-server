package com.lineinc.erp.api.server.interfaces.rest.v2.fuelaggregation.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.fuelaggregation.service.v2.FuelAggregationV2Service;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationChangeHistoryResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.PageableUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/fuel-aggregations")
@RequiredArgsConstructor
@Tag(name = "유류집계 관리 V2")
public class FuelAggregationV2Controller extends BaseController {

    private final FuelAggregationV2Service fuelAggregationV2Service;

    @Operation(summary = "유류집계 변경 이력 조회")
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_FUEL_AGGREGATION, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<FuelAggregationChangeHistoryResponse>>> getFuelAggregationChangeHistoriesWithPaging(
            @PathVariable final Long id,
            @AuthenticationPrincipal final CustomUserDetails loginUser,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest) {
        final Page<FuelAggregationChangeHistoryResponse> page = fuelAggregationV2Service
                .getFuelAggregationChangeHistoriesWithPaging(
                        id, loginUser, PageableUtils.createPageable(pageRequest, sortRequest));
        return SuccessResponse.ok(PagingResponse.from(page));
    }
}