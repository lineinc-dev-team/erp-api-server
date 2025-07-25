package com.lineinc.erp.api.server.presentation.v1.steelmanagement.controller;

import com.lineinc.erp.api.server.application.steelmanagement.SteelManagementService;
import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.common.request.PageRequest;
import com.lineinc.erp.api.server.common.request.SortRequest;
import com.lineinc.erp.api.server.common.response.PagingInfo;
import com.lineinc.erp.api.server.common.response.PagingResponse;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostListRequest;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.response.ManagementCostResponse;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.ApproveSteelManagementRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.DeleteSteelManagementRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementListRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.response.SteelManagementResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/steel-management")
@RequiredArgsConstructor
@Tag(name = "Steel Management", description = "강재 관리 API")
public class SteelManagementController {
    private final SteelManagementService steelManagementService;

    @Operation(summary = "강재 관리 등록", description = "강재 관리 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류")
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.CREATE)
    public ResponseEntity<SuccessResponse<Long>> createSteelManagement(
            @Valid @RequestBody SteelManagementCreateRequest request) {
        steelManagementService.createSteelManagement(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강재 관리 목록 조회", description = "등록된 강재 관리 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류")
    })
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<SteelManagementResponse>>> getSteelManagementList(
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid SteelManagementListRequest request
    ) {
        Page<SteelManagementResponse> page = steelManagementService.getSteelManagementList(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort())
        );

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())
        ));
    }

    @Operation(
            summary = "강재 관리 삭제",
            description = "하나 이상의 강재 관리 ID를 받아 해당 데이터를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "강재 관리를 찾을 수 없음")
    })
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteSteelManagements(
            @Valid @RequestBody DeleteSteelManagementRequest request
    ) {
        steelManagementService.deleteSteelManagements(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강재 관리 승인 처리", description = "하나 이상의 강재 관리 ID를 받아 구분값을 승인으로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인 처리 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "강재 관리를 찾을 수 없음")
    })
    @PatchMapping("/approve")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.APPROVE)
    public ResponseEntity<Void> approveSteelManagement(
            @Valid @RequestBody ApproveSteelManagementRequest request
    ) {
        steelManagementService.approveSteelManagements(request);
        return ResponseEntity.ok().build();
    }
}