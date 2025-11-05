package com.lineinc.erp.api.server.interfaces.rest.v1.site.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.site.enums.SiteFileType;
import com.lineinc.erp.api.server.domain.site.enums.SiteType;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.CreateSiteRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.DeleteSitesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.SiteDownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.SiteListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.UpdateSiteRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteFileTypeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteTypeResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SliceInfo;
import com.lineinc.erp.api.server.shared.dto.response.SliceResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.DownloadFieldUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.lineinc.erp.api.server.shared.util.ResponseHeaderUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sites")
@RequiredArgsConstructor
@Tag(name = "현장 관리")
public class SiteController extends BaseController {
    private final SiteService siteService;

    @Operation(summary = "현장 등록")
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createSite(
            @Valid @RequestBody final CreateSiteRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        siteService.createSite(request, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "현장 목록 조회")
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<SiteResponse>>> getAllSites(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final SiteListRequest request) {
        final Page<SiteResponse> page = siteService.getAllSites(
                userDetails.getUserId(),
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "현장 목록 엑셀 다운로드")
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.EXCEL_DOWNLOAD)
    public void downloadSitesExcel(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid final SortRequest sortRequest,
            @Valid final SiteListRequest request,
            @Valid final SiteDownloadRequest siteDownloadRequest,
            final HttpServletResponse response) throws IOException {
        final List<String> parsed = DownloadFieldUtils.parseFields(siteDownloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed, SiteDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "현장 목록.xlsx");

        try (Workbook workbook = siteService.downloadExcel(
                userDetails.getUserId(),
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed)) {
            workbook.write(response.getOutputStream());
        }
    }

    @Operation(summary = "현장 삭제")
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteSites(
            @RequestBody final DeleteSitesRequest siteIds) {
        siteService.deleteSites(siteIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "현장 상세 조회")
    @GetMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SiteDetailResponse>> getSiteDetail(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @PathVariable final Long id) {
        final SiteDetailResponse siteResponse = siteService.getSiteById(id, userDetails.getUserId());
        return ResponseEntity.ok(SuccessResponse.of(siteResponse));
    }

    @Operation(summary = "현장 정보 수정")
    @PatchMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateSite(
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateSiteRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        siteService.updateSite(id, request, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "현장 이름 키워드 검색")
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<SliceResponse<SiteResponse.SiteSimpleResponse>>> searchClientCompanyByName(
            @AuthenticationPrincipal final CustomUserDetails user,
            @Valid final SortRequest sortRequest,
            @Valid final PageRequest pageRequest,
            @RequestParam(required = false) final String keyword) {
        final Slice<SiteResponse.SiteSimpleResponse> slice = siteService.searchSiteByName(user.getUserId(), keyword,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "현장 구분 목록 조회")
    @GetMapping("/site-types")
    public ResponseEntity<SuccessResponse<List<SiteTypeResponse>>> getSiteTypes() {
        final List<SiteTypeResponse> responseList = Arrays.stream(SiteType.values())
                .map(type -> new SiteTypeResponse(type.name(), type.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "현장 파일 구분 목록 조회")
    @GetMapping("/site-file-types")
    public ResponseEntity<SuccessResponse<List<SiteFileTypeResponse>>> getSiteFileTypes() {
        final List<SiteFileTypeResponse> responseList = Arrays.stream(SiteFileType.values())
                .map(type -> new SiteFileTypeResponse(type.name(), type.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "현장 변경 이력 조회")
    @GetMapping("/{id}/change-histories")
    public ResponseEntity<SuccessResponse<SliceResponse<SiteChangeHistoryResponse>>> getSiteChangeHistories(
            @PathVariable final Long id,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @AuthenticationPrincipal final CustomUserDetails user) {
        final Slice<SiteChangeHistoryResponse> slice = siteService.getSiteChangeHistories(id,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()),
                user.getUserId());
        return ResponseEntity.ok(SuccessResponse.of(new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }
}
