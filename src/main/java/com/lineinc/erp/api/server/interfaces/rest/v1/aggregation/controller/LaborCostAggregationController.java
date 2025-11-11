package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.aggregation.laborcost.service.LaborCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.outsourcinglaborcost.service.OutsourcingLaborCostAggregationService;
import com.lineinc.erp.api.server.domain.laborpayroll.service.v1.LaborPayrollService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.LaborCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.LaborPayrollDetailAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.OutsourcingLaborCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.LaborCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.OutsourcingLaborCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollDetailResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 노무비 집계 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/aggregation")
@RequiredArgsConstructor
@Tag(name = "집계")
public class LaborCostAggregationController extends BaseController {

    private final LaborCostAggregationService laborCostAggregationService;
    private final OutsourcingLaborCostAggregationService outsourcingLaborCostAggregationService;
    private final LaborPayrollService laborPayrollService;

    @GetMapping("/labor-cost")
    @Operation(summary = "노무비 조회")
    @RequireMenuPermission(menu = AppConstants.MENU_AGGREGATION_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<LaborCostAggregationResponse>> getLaborCostAggregation(
            @Valid final LaborCostAggregationRequest request) {
        final LaborCostAggregationResponse response = laborCostAggregationService.getLaborCostAggregation(
                request.siteId(), request.siteProcessId(), request.yearMonth(), request.laborType());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @GetMapping("/outsourcing-labor-cost")
    @Operation(summary = "노무비 조회 (용역업체별)")
    @RequireMenuPermission(menu = AppConstants.MENU_AGGREGATION_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<OutsourcingLaborCostAggregationResponse>> getOutsourcingLaborCostAggregation(
            @Valid final OutsourcingLaborCostAggregationRequest request) {
        final OutsourcingLaborCostAggregationResponse response = outsourcingLaborCostAggregationService
                .getOutsourcingLaborCostAggregation(request.siteId(), request.siteProcessId(), request.yearMonth());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    /**
     * 노무명세서 상세 조회
     * 현장, 공정, 년월, 노무인력 타입, 일자로 필터링하여 조회
     */
    @Operation(summary = "노무명세서 조회")
    @GetMapping("/labor-payroll")
    @RequireMenuPermission(menu = AppConstants.MENU_AGGREGATION_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<List<LaborPayrollDetailResponse>>> getLaborPayrollDetails(
            @Parameter(description = "조회 조건") @ModelAttribute final LaborPayrollDetailAggregationRequest request) {
        final List<LaborPayrollDetailResponse> result = laborPayrollService.getLaborPayrollDetails(
                request.siteId(), request.siteProcessId(), request.yearMonth(), request.type());
        return ResponseEntity.ok(SuccessResponse.of(result));
    }

}
