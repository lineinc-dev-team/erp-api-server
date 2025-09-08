package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollListResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// /**
// * 노무명세서 관리 API Controller
// */
// @Tag(name = "노무명세서 관리", description = "노무명세서 조회 관련 API")
// @RestController
// @RequestMapping("/api/v1/labor-payrolls")
// @RequiredArgsConstructor
// public class LaborPayrollController {

// private final LaborPayrollService laborPayrollService;

// /**
// * 노무명세서 조회
// * 현장별, 공정별, 월별 일별 근무 내역 및 급여 정보 조회
// */
// @Operation(summary = "노무명세서 조회", description = "노무명세서를 조회합니다.")
// @ApiResponses({
// @ApiResponse(responseCode = "200", description = "조회 성공"),
// @ApiResponse(responseCode = "400", description = "입력값 오류", content =
// @Content())
// })
// @GetMapping
// @RequireMenuPermission(menu = AppConstants.MENU_LABOR_PAYROLL, action =
// PermissionAction.VIEW)
// public ResponseEntity<SuccessResponse<LaborPayrollListResponse>>
// getLaborPayrollList(
// @Parameter(description = "조회 조건") @ModelAttribute LaborPayrollSearchRequest
// request) {

// LaborPayrollListResponse result =
// laborPayrollService.getLaborPayrollList(request);
// return ResponseEntity.ok(SuccessResponse.of(result));
// }
// }
