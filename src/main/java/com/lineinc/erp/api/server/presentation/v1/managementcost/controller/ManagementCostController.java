package com.lineinc.erp.api.server.presentation.v1.managementcost.controller;

import com.lineinc.erp.api.server.application.managementcost.ManagementCostService;
import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostListRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/management-costs")
@RequiredArgsConstructor
@Tag(name = "Management Cost", description = "관리비 관련 API")
public class ManagementCostController {

    private final ManagementCostService managementCostService;

    @Operation(
            summary = "관리비 등록",
            description = "관리비 정보를 등록합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리비 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 현장 또는 공정을 등록하려는 경우")
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createManagementCost(
            @Valid @RequestBody ManagementCostCreateRequest request
    ) {
        managementCostService.createManagementCost(request);
        return ResponseEntity.ok().build();
    }

//    @Operation(
//            summary = "관리비 목록 조회",
//            description = "필터 조건에 맞는 관리비 목록을 조회합니다"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "관리비 목록 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
//    })
//    @GetMapping
//    @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action = PermissionAction.VIEW)
//    public ResponseEntity<List<ManagementCostResponse>> getManagementCosts(
//            @Valid ManagementCostListRequest request
//    ) {
//        List<ManagementCostListRequest> response = managementCostService.getManagementCosts(request);
//        return ResponseEntity.ok(response);
//    }
}
