package com.lineinc.erp.api.server.presentation.v1.materialmanagement.controller;

import com.lineinc.erp.api.server.application.materialmanagement.MaterialManagementService;
import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request.MaterialManagementCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/material-managements")
@RequiredArgsConstructor
@Tag(name = "Material Management", description = "자재관리 관련 API")
public class MaterialManagementController {

    private final MaterialManagementService materialManagementService;

    @Operation(
            summary = "자재관리 등록",
            description = "자재관리 정보를 등록합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "자재관리 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 현장 또는 공정을 등록하려는 경우", content = @Content())
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createMaterialManagement(
            @Valid @RequestBody MaterialManagementCreateRequest request
    ) {
        materialManagementService.createMaterialManagement(request);
        return ResponseEntity.ok().build();
    }
}
