package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.steelmanagementv2.service.SteelManagementV2Service;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2CreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2ListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementChangeHistoryV2Response;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementV2Response;
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
@RequestMapping("/api/v2/steel-managements")
@RequiredArgsConstructor
@Tag(name = "강재수불부 관리 V2")
public class SteelManagementV2Controller extends BaseController {
    private final SteelManagementV2Service steelManagementV2Service;

    @Operation(summary = "강재수불부 등록")
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createSteelManagementV2(
            @Valid @RequestBody final SteelManagementV2CreateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        steelManagementV2Service.createSteelManagementV2(request, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강재수불부 목록 조회")
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<SteelManagementV2Response>>> getSteelManagementV2List(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final SteelManagementV2ListRequest request) {
        final Page<SteelManagementV2Response> page = steelManagementV2Service.getSteelManagementV2List(
                request,
                PageableUtils.createPageable(pageRequest, sortRequest));
        return SuccessResponse.ok(PagingResponse.from(page));
    }

    @Operation(summary = "강재수불부 변경 이력 조회")
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<SteelManagementChangeHistoryV2Response>>> getSteelManagementChangeHistories(
            @PathVariable final Long id,
            @AuthenticationPrincipal final CustomUserDetails loginUser,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest) {
        final Page<SteelManagementChangeHistoryV2Response> page = steelManagementV2Service
                .getSteelManagementChangeHistoriesWithPaging(
                        id, loginUser, PageableUtils.createPageable(pageRequest, sortRequest));
        return SuccessResponse.ok(PagingResponse.from(page));
    }
}
