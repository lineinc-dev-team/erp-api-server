package com.lineinc.erp.api.server.interfaces.rest.v2.managementcost.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.managementcost.service.v2.ManagementCostV2Service;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostChangeHistoryResponse;
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
@RequestMapping("/api/v2/management-costs")
@RequiredArgsConstructor
@Tag(name = "관리비 관리 V2")
public class ManagementV2CostController extends BaseController {

    private final ManagementCostV2Service managementCostV2Service;

    @Operation(summary = "관리비 변경 이력 조회")
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<ManagementCostChangeHistoryResponse>>> getManagementCostChangeHistoriesWithPaging(
            @PathVariable final Long id,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @AuthenticationPrincipal final CustomUserDetails user) {
        final Page<ManagementCostChangeHistoryResponse> page = managementCostV2Service
                .getManagementCostChangeHistoriesWithPaging(
                        id, PageableUtils.createPageable(pageRequest, sortRequest), user.getUserId());
        return SuccessResponse.ok(PagingResponse.from(page));
    }
}
