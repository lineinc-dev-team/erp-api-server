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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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

    private final LaborPayrollService laborPayrollService;

    /**
     * 노무명세서 목록 조회
     * 현장별, 공정별, 월별로 그룹핑된 노무비 통계 정보 조회
     */
    @Operation(summary = "노무명세서 목록 조회", description = "월별로 그룹핑된 노무명세서 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
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
    @Operation(summary = "노무명세서 엑셀 다운로드", description = "검색 조건에 맞는 노무명세서 목록을 엑셀 파일로 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
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
    @Operation(summary = "노무명세서 상세 조회", description = "현장, 공정, 년월, 노무인력 타입, 일자로 필터링하여 노무명세서 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 조건의 노무명세서를 찾을 수 없음", content = @Content())
    })
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
    @Operation(summary = "노무명세서 집계 상세 조회", description = "노무명세서 집계 데이터를 조회합니다.")

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 집계 데이터를 찾을 수 없음", content = @Content())
    })
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
    @Operation(summary = "노무명세서 집계 테이블 수정", description = "노무명세서 집계 테이블을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 집계 데이터를 찾을 수 없음", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
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
    @Operation(summary = "노무명세서 수정", description = "노무명세서들을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 노무명세서를 찾을 수 없음", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
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
    @Operation(summary = "노무명세서 변경이력 조회", description = "특정 노무명세서 집계와 관련된 변경이력을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
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
    @Operation(summary = "노무명세서 변경이력 수정", description = "특정 변경이력의 메모를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 변경이력을 찾을 수 없음", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @PatchMapping("/change-histories/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_PAYROLL, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateLaborPayrollChangeHistory(
            @Parameter(description = "변경이력 ID") @PathVariable final Long id,
            @Parameter(description = "수정 요청") @Valid @RequestBody final LaborPayrollChangeHistoryUpdateRequest request) {
        laborPayrollService.updateLaborPayrollChangeHistory(id, request);
        return ResponseEntity.ok().build();
    }
}
