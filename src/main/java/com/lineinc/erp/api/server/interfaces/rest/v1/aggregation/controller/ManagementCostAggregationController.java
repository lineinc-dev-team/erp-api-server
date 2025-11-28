package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lineinc.erp.api.server.domain.aggregation.managementcost.service.ManagementCostAggregationService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ManagementCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ManagementCostMealFeeOutsourcingCompaniesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.MealFeeAggregationDetailRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MealFeeAggregationDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/aggregation/management-cost")
@Tag(name = "집계")
public class ManagementCostAggregationController extends BaseController {

    private final ManagementCostAggregationService managementCostAggregationService;

    @GetMapping
    @Operation(summary = "관리비 조회")
    @RequireMenuPermission(menu = AppConstants.MENU_AGGREGATION_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<ManagementCostAggregationResponse>> getManagementCostAggregation(
            @Valid final ManagementCostAggregationRequest request) {
        final ManagementCostAggregationResponse response =
                managementCostAggregationService.getManagementCostAggregation(request);
        return SuccessResponse.ok(response);
    }

    @GetMapping("/meal-fee-outsourcing-companies")
    @Operation(summary = "식대 외주업체 목록 조회")
    public ResponseEntity<SuccessResponse<List<CompanyResponse.CompanySimpleResponse>>> getMealFeeOutsourcingCompanies(
            @Valid final ManagementCostMealFeeOutsourcingCompaniesRequest request) {
        final var responseList = managementCostAggregationService.getMealFeeOutsourcingCompanies(request);
        return SuccessResponse.ok(responseList);
    }

    @GetMapping("/meal-fee-detail")
    @Operation(summary = "식대 상세 조회")
    @RequireMenuPermission(menu = AppConstants.MENU_AGGREGATION_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<MealFeeAggregationDetailResponse>> getMealFeeAggregationDetail(
            @Valid final MealFeeAggregationDetailRequest request) {
        final MealFeeAggregationDetailResponse response =
                managementCostAggregationService.getMealFeeAggregationDetail(request);
        return SuccessResponse.ok(response);
    }
}
