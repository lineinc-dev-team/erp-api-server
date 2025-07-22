package com.lineinc.erp.api.server.presentation.v1.site.controller;

import com.lineinc.erp.api.server.application.site.SiteService;

import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sites")
@RequiredArgsConstructor
@Tag(name = "Sites", description = "현장 관련 API")
public class SiteController {
    private final SiteService siteService;

    @Operation(summary = "현장 등록", description = "현장 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현장 등록 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저 또는 발주처를 등록하려는 경우"),
            @ApiResponse(responseCode = "400", description = "입력값 오류")
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createSite(@Valid @RequestBody SiteCreateRequest request) {
        siteService.createSite(request);
        return ResponseEntity.ok().build();
    }
}
