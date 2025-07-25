package com.lineinc.erp.api.server.presentation.v1.steelmanagement.controller;

import com.lineinc.erp.api.server.application.steelmanagement.SteelManagementService;
import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.common.request.PageRequest;
import com.lineinc.erp.api.server.common.request.SortRequest;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostListRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementListRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.response.SteelManagementResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

//    @Operation(summary = "강재 관리 목록 조회", description = "등록된 강재 관리 목록을 조회합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "조회 성공"),
//            @ApiResponse(responseCode = "400", description = "입력값 오류")
//    })
//    @GetMapping
//    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.VIEW)
//    public ResponseEntity<SuccessResponse<SteelManagementResponse>> getSteelManagementList(
//            @Valid PageRequest pageRequest,
//            @Valid SortRequest sortRequest,
//            @Valid SteelManagementListRequest request
//    ) {
//        List<SteelManagementResponse> list = steelManagementService.getSteelManagementList();
//        return ResponseEntity.ok(SuccessResponse.of(list));
//    }
}