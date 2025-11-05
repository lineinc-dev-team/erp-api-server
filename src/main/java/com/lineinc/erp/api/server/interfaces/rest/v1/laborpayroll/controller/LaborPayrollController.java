package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.controller;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.laborpayroll.service.v1.LaborPayrollService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollChangeHistoryUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollDetailSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollDownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollSummaryUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollSummaryResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SliceInfo;
import com.lineinc.erp.api.server.shared.dto.response.SliceResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.DownloadFieldUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.lineinc.erp.api.server.shared.util.ResponseHeaderUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 노무명세서 관리 API Controller
 */
@Tag(name = "노무명세서 관리")
@RestController
@RequestMapping("/api/v1/labor-payrolls")
@RequiredArgsConstructor
public class LaborPayrollController extends BaseController {

    private final LaborPayrollService laborPayrollService;

    /**
     * 노무명세서 목록 조회
     * 현장별, 공정별, 월별로 그룹핑된 노무비 통계 정보 조회
     */
    @Operation(summary = "노무명세서 목록 조회")
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_PAYROLL, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<LaborPayrollSummaryResponse>>> getLaborPayrollMonthlyList(
            @AuthenticationPrincipal final CustomUserDetails user,
            @Parameter(description = "조회 조건") @ModelAttribute final LaborPayrollSearchRequest request,
            @Parameter(description = "페이징 정보") @ModelAttribute final PageRequest pageRequest,
            @Parameter(description = "정렬 정보") @ModelAttribute final SortRequest sortRequest) {

        final PagingResponse<LaborPayrollSummaryResponse> result = laborPayrollService
                .getLaborPayrollMonthlyList(user.getUserId(), request, pageRequest, sortRequest);
        return ResponseEntity.ok(SuccessResponse.of(result));
    }

    /**
     * 노무명세서 엑셀 다운로드
     * 검색 조건에 맞는 노무명세서 목록을 엑셀 파일로 다운로드
     */
    @Operation(summary = "노무명세서 엑셀 다운로드")
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_PAYROLL, action = PermissionAction.EXCEL_DOWNLOAD)
    public void downloadLaborPayrollExcel(
            @AuthenticationPrincipal final CustomUserDetails user,
            @Parameter(description = "정렬 정보") @ModelAttribute final SortRequest sortRequest,
            @Parameter(description = "조회 조건") @ModelAttribute final LaborPayrollSearchRequest request,
            @Parameter(description = "다운로드 필드") @ModelAttribute final LaborPayrollDownloadRequest downloadRequest,
            final HttpServletResponse response) throws IOException {
        final List<String> parsed = DownloadFieldUtils.parseFields(downloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed, LaborPayrollDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "노무명세서 목록.xlsx");

        try (Workbook workbook = laborPayrollService.downloadExcel(
                user,
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed)) {
            workbook.write(response.getOutputStream());
        }
    }

    /**
     * 노무명세서 상세 조회
     * 현장, 공정, 년월, 노무인력 타입, 일자로 필터링하여 조회
     */
    @Operation(summary = "노무명세서 상세 조회")
    @GetMapping("/details")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_PAYROLL, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<List<LaborPayrollDetailResponse>>> getLaborPayrollDetails(
            @Parameter(description = "조회 조건") @ModelAttribute final LaborPayrollDetailSearchRequest request) {
        final List<LaborPayrollDetailResponse> result = laborPayrollService.getLaborPayrollDetails(
                request.siteId(), request.processId(), request.yearMonth(), request.type());
        return ResponseEntity.ok(SuccessResponse.of(result));
    }

    /**
     * 노무명세서 집계 상세 조회
     */
    @Operation(summary = "노무명세서 집계 상세 조회")
    @GetMapping("/summary/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_PAYROLL, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<LaborPayrollSummaryResponse>> getLaborPayrollSummaryDetail(
            @Parameter(description = "집계 ID") @PathVariable final Long id) {
        final LaborPayrollSummaryResponse result = laborPayrollService.getLaborPayrollSummaryDetail(id);
        return ResponseEntity.ok(SuccessResponse.of(result));
    }

    /**
     * 노무명세서 집계 테이블 수정
     */
    @Operation(summary = "노무명세서 집계 테이블 수정")
    @PatchMapping("/summary/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_PAYROLL, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateLaborPayrollSummary(
            @Parameter(description = "집계 ID") @PathVariable final Long id,
            @Parameter(description = "수정 요청") @Valid @RequestBody final LaborPayrollSummaryUpdateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        laborPayrollService.updateLaborPayrollSummary(id, request, user.getUserId());
        return ResponseEntity.ok().build();
    }

    /**
     * 노무명세서 수정
     */
    @Operation(summary = "노무명세서 수정")
    @PatchMapping
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_PAYROLL, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateLaborPayrolls(
            @Parameter(description = "수정 요청") @Valid @RequestBody final LaborPayrollUpdateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        laborPayrollService.updateLaborPayrolls(request, user.getUserId());
        return ResponseEntity.ok().build();
    }

    /**
     * 노무명세서 변경이력 조회
     */
    @Operation(summary = "노무명세서 변경이력 조회")
    @GetMapping("/summary/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_PAYROLL, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<LaborPayrollChangeHistoryResponse>>> getLaborPayrollChangeHistories(
            @Parameter(description = "노무명세서 집계 ID") @PathVariable final Long id,
            @Parameter(description = "페이징 정보") @ModelAttribute final PageRequest pageRequest,
            @Parameter(description = "정렬 정보") @ModelAttribute final SortRequest sortRequest,
            @AuthenticationPrincipal final CustomUserDetails user) {

        final var slice = laborPayrollService.getLaborPayrollChangeHistories(
                id,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()),
                user.getUserId());

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    /**
     * 노무명세서 변경이력 수정
     */
    @Operation(summary = "노무명세서 변경이력 수정")
    @PatchMapping("/change-histories/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_PAYROLL, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateLaborPayrollChangeHistory(
            @Parameter(description = "변경이력 ID") @PathVariable final Long id,
            @Parameter(description = "수정 요청") @Valid @RequestBody final LaborPayrollChangeHistoryUpdateRequest request) {
        laborPayrollService.updateLaborPayrollChangeHistory(id, request);
        return ResponseEntity.ok().build();
    }
}
