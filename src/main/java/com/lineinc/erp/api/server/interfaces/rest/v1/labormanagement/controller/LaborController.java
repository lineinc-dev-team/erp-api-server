package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.labormanagement.service.LaborService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborCreateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/labors")
@RequiredArgsConstructor
@Tag(name = "노무 관리", description = "노무 관련 API")
public class LaborController {

    private final LaborService laborService;

    @Operation(summary = "노무 인력정보 등록", description = "노무 인력정보를 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "노무 등록 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_MANAGEMENT, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createLabor(@Valid @RequestBody LaborCreateRequest request) {
        laborService.createLabor(request);
        return ResponseEntity.ok().build();
    }
}
