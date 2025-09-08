package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 노무명세서 관리 API Controller
 */
@Tag(name = "노무명세서 관리", description = "노무명세서 조회 관련 API")
@RestController
@RequestMapping("/api/v1/labor-payrolls")
@RequiredArgsConstructor
public class LaborPayrollController {

    // /**
    // * 노무명세서 목록 조회
    // * 출역일보 데이터를 기반으로 실시간 노무비 계산하여 조회
    // */
    // @Operation(summary = "노무명세서 목록 조회", description = "지정된 기간 동안의 노무명세서를 조회합니다.")
    // @ApiResponses({
    // @ApiResponse(responseCode = "200", description = "조회 성공"),
    // @ApiResponse(responseCode = "400", description = "입력값 오류", content =
    // @Content())
    // })
    // @GetMapping
    // @RequireMenuPermission(menu = AppConstants.MENU_LABOR_PAYROLL, action =
    // PermissionAction.VIEW)
    // public ResponseEntity<Page<LaborPayrollResponse>> getLaborPayrollList(
    // @Parameter(description = "조회 조건") @Valid LaborPayrollSearchRequest request,
    // @Parameter(description = "페이징 정보") @PageableDefault(size = 20) Pageable
    // pageable) {

    // Page<LaborPayrollResponse> result =
    // laborPayrollService.getLaborPayrollList(request, pageable);
    // return ResponseEntity.ok(result);
    // }
}
