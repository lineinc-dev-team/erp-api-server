package com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.sitemanagementcost.service.v1.SiteManagementCostService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response.SiteManagementCostResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 현장관리비 Controller
 */
@RestController
@RequestMapping("/api/v1/site-management-costs")
@RequiredArgsConstructor
@Tag(name = "현장관리비", description = "현장관리비 관련 API")
public class SiteManagementCostController extends BaseController {

    private final SiteManagementCostService siteManagementCostService;

    @PostMapping
    @Operation(summary = "현장관리비 생성", description = "년월별 현장/공정 관리비를 생성합니다.")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE_MANAGEMENT_COST, action = PermissionAction.CREATE)
    public ResponseEntity<SuccessResponse<SiteManagementCostResponse>> createSiteManagementCost(
            @Valid @RequestBody final SiteManagementCostCreateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        final SiteManagementCostResponse response = siteManagementCostService.createSiteManagementCost(request, user);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}
