package com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.controller;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.sitemanagementcost.service.v1.SiteManagementCostService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostDeleteRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostDownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response.SiteManagementCostDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response.SiteManagementCostResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.DownloadFieldUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.lineinc.erp.api.server.shared.util.ResponseHeaderUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 현장관리비 Controller
 */
@RestController
@RequestMapping("/api/v1/site-management-costs")
@RequiredArgsConstructor
@Tag(name = "현장관리비", description = "현장관리비 관련 API")
public class SiteManagementCostController extends BaseController {

    private final SiteManagementCostService siteManagementCostService;

    @GetMapping
    @Operation(summary = "현장관리비 목록 조회", description = "현장관리비 목록을 조회합니다.")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE_MANAGEMENT_COST, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<SiteManagementCostResponse>>> getSiteManagementCostList(
            @Valid final SiteManagementCostListRequest request,
            @Valid final SortRequest sortRequest,
            @Valid final PageRequest pageRequest) {
        final Page<SiteManagementCostResponse> page = siteManagementCostService.getSiteManagementCostList(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));
        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "현장관리비 상세 조회", description = "ID로 현장관리비 상세 정보를 조회합니다.")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE_MANAGEMENT_COST, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SiteManagementCostDetailResponse>> getSiteManagementCostDetail(
            @PathVariable final Long id) {
        final SiteManagementCostDetailResponse response = siteManagementCostService.getSiteManagementCostDetail(id);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @PostMapping
    @Operation(summary = "현장관리비 생성", description = "년월별 현장/공정 관리비를 생성합니다.")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE_MANAGEMENT_COST, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createSiteManagementCost(
            @Valid @RequestBody final SiteManagementCostCreateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        siteManagementCostService.createSiteManagementCost(request, user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "현장관리비 수정", description = "현장관리비 정보를 수정합니다.")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE_MANAGEMENT_COST, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateSiteManagementCost(
            @PathVariable final Long id,
            @Valid @RequestBody final SiteManagementCostUpdateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        siteManagementCostService.updateSiteManagementCost(id, request, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Operation(summary = "현장관리비 삭제", description = "하나 이상의 현장관리비 ID를 받아 해당 관리비를 삭제합니다.")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE_MANAGEMENT_COST, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteSiteManagementCosts(
            @Valid @RequestBody final SiteManagementCostDeleteRequest request) {
        siteManagementCostService.deleteSiteManagementCosts(request.siteManagementCostIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download")
    @Operation(summary = "현장관리비 목록 엑셀 다운로드", description = "검색 조건에 맞는 현장관리비 목록을 엑셀 파일로 다운로드합니다.")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE_MANAGEMENT_COST, action = PermissionAction.EXCEL_DOWNLOAD)
    public void downloadSiteManagementCostsExcel(
            @AuthenticationPrincipal final CustomUserDetails user,
            @Valid final SortRequest sortRequest,
            @Valid final SiteManagementCostListRequest request,
            @Valid final SiteManagementCostDownloadRequest downloadRequest,
            final HttpServletResponse response) throws IOException {
        final List<String> parsed = DownloadFieldUtils.parseFields(downloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed, SiteManagementCostDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "현장관리비 목록.xlsx");

        try (Workbook workbook = siteManagementCostService.downloadExcel(
                user,
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed)) {
            workbook.write(response.getOutputStream());
        }
    }
}
