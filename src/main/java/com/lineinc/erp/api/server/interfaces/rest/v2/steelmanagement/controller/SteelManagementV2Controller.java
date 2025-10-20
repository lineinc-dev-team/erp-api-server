package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.controller;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type;
import com.lineinc.erp.api.server.domain.steelmanagementv2.service.SteelManagementV2Service;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2CreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2DetailDownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2DownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2ListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2UpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementChangeHistoryV2Response;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementV2DetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementV2Response;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.DownloadFieldUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.lineinc.erp.api.server.shared.util.ResponseHeaderUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/steel-managements")
@RequiredArgsConstructor
@Tag(name = "강재수불부 관리 V2")
public class SteelManagementV2Controller extends BaseController {
    private final SteelManagementV2Service steelManagementV2Service;

    @Operation(summary = "강재수불부 등록")
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createSteelManagementV2(
            @Valid @RequestBody final SteelManagementV2CreateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        steelManagementV2Service.createSteelManagementV2(request, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강재수불부 목록 조회")
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<SteelManagementV2Response>>> getSteelManagementV2List(
            @AuthenticationPrincipal final CustomUserDetails user,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final SteelManagementV2ListRequest request) {
        final Page<SteelManagementV2Response> page = steelManagementV2Service.getSteelManagementV2List(request,
                user.getUserId(),
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));
        return SuccessResponse.ok(PagingResponse.from(page));
    }

    @Operation(summary = "강재수불부 상세 조회")
    @GetMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SteelManagementV2DetailResponse>> getSteelManagementV2Detail(
            @PathVariable final Long id,
            @RequestParam(required = false) @Schema(description = "타입 필터 (입고/출고/사장/고철)", example = "INCOMING") final SteelManagementDetailV2Type type) {
        final SteelManagementV2DetailResponse response = steelManagementV2Service.getSteelManagementV2ById(id, type);
        return SuccessResponse.ok(response);
    }

    @Operation(summary = "강재수불부 수정")
    @PatchMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateSteelManagementV2(
            @PathVariable final Long id,
            @Valid @RequestBody final SteelManagementV2UpdateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        steelManagementV2Service.updateSteelManagementV2(id, request, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강재수불부 목록 엑셀 다운로드", description = "검색 조건에 맞는 강재수불부 목록을 엑셀 파일로 다운로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.EXCEL_DOWNLOAD)
    public void downloadSteelManagementsExcel(
            @AuthenticationPrincipal final CustomUserDetails user,
            @Valid final SortRequest sortRequest,
            @Valid final SteelManagementV2ListRequest request,
            @Valid final SteelManagementV2DownloadRequest downloadRequest,
            final HttpServletResponse response) throws IOException {
        final List<String> parsed = DownloadFieldUtils.parseFields(downloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed, SteelManagementV2DownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "강재수불부 목록.xlsx");
        try (Workbook workbook = steelManagementV2Service.downloadExcel(
                user,
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed)) {
            workbook.write(response.getOutputStream());
        }
    }

    @Operation(summary = "강재수불부 상세 엑셀 다운로드")
    @GetMapping("/{id}/download")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.EXCEL_DOWNLOAD)
    public void downloadSteelManagementDetailExcel(
            @PathVariable final Long id,
            @AuthenticationPrincipal final CustomUserDetails user,
            @Valid final SteelManagementV2DetailDownloadRequest downloadRequest,
            final HttpServletResponse response) throws IOException {
        final List<String> fieldsToUse = downloadRequest.getFieldsToUse();
        DownloadFieldUtils.validateFields(fieldsToUse, SteelManagementV2DetailDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "강재수불부 상세 목록.xlsx");
        try (Workbook workbook = steelManagementV2Service.downloadDetailExcel(
                id,
                user,
                fieldsToUse)) {
            workbook.write(response.getOutputStream());
        }
    }

    @Operation(summary = "강재수불부 변경 이력 조회")
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<SteelManagementChangeHistoryV2Response>>> getSteelManagementChangeHistories(
            @PathVariable final Long id,
            @AuthenticationPrincipal final CustomUserDetails loginUser,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest) {
        final Page<SteelManagementChangeHistoryV2Response> page = steelManagementV2Service
                .getSteelManagementChangeHistoriesWithPaging(
                        id, loginUser, PageableUtils.createPageable(pageRequest, sortRequest));
        return SuccessResponse.ok(PagingResponse.from(page));
    }
}
