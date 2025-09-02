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
import com.lineinc.erp.api.server.domain.site.service.SiteService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sites")
@RequiredArgsConstructor
@Tag(name = "현장 관리", description = "현장 관련 API")
public class SiteController {
    private final SiteService siteService;

    @Operation(summary = "현장 등록", description = "현장 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현장 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저 또는 발주처를 등록하려는 경우")
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createSite(@Valid @RequestBody CreateSiteRequest request) {
        siteService.createSite(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "현장 목록 조회", description = "등록된 모든 현장 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현장 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<SiteResponse>>> getAllSites(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid SiteListRequest request) {
        Page<SiteResponse> page = siteService.getAllSites(
                userDetails.getUserId(),
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "현장 목록 엑셀 다운로드", description = "검색 조건에 맞는 현장 목록을 엑셀 파일로 다운로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.VIEW)
    public void downloadSitesExcel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid SortRequest sortRequest,
            @Valid SiteListRequest request,
            @Valid SiteDownloadRequest siteDownloadRequest,
            HttpServletResponse response) throws IOException {
        List<String> parsed = DownloadFieldUtils.parseFields(siteDownloadRequest.fields());
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

    @Operation(summary = "현장 삭제", description = "하나 이상의 현장 ID를 받아 해당 현장을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "현장을 찾을 수 없음")
    })
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteSites(
            @RequestBody DeleteSitesRequest siteIds) {
        siteService.deleteSites(siteIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "현장 상세 조회", description = "현장 상세 정보를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현장 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "현장을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SiteDetailResponse>> getSiteDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        SiteDetailResponse siteResponse = siteService.getSiteById(id, userDetails.getUserId());
        return ResponseEntity.ok(SuccessResponse.of(siteResponse));
    }

    @Operation(summary = "현장 정보 수정", description = "기존 현장 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "해당 현장을 찾을 수 없음")
    })
    @PatchMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateSite(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSiteRequest request) {
        siteService.updateSite(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "현장 이름 키워드 검색", description = "현장명으로 간단한 검색을 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<SliceResponse<SiteResponse.SiteSimpleResponse>>> searchClientCompanyByName(
            @Valid SortRequest sortRequest,
            @Valid PageRequest pageRequest,
            @RequestParam(required = false) String keyword) {
        Slice<SiteResponse.SiteSimpleResponse> slice = siteService.searchSiteByName(keyword,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "현장 구분 목록 조회", description = "현장 구분 목록을 반환합니다")
    @GetMapping("/site-types")
    public ResponseEntity<SuccessResponse<List<SiteTypeResponse>>> getSiteTypes() {
        List<SiteTypeResponse> responseList = Arrays.stream(SiteType.values())
                .map(type -> new SiteTypeResponse(type.name(), type.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "현장 파일 구분 목록 조회", description = "현장 파일 구분 목록을 반환합니다")
    @GetMapping("/site-file-types")
    public ResponseEntity<SuccessResponse<List<SiteFileTypeResponse>>> getSiteFileTypes() {
        List<SiteFileTypeResponse> responseList = Arrays.stream(SiteFileType.values())
                .map(type -> new SiteFileTypeResponse(type.name(), type.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "현장 변경 이력 조회", description = "특정 현장의 변경 이력을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "현장을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}/change-histories")
    public ResponseEntity<SuccessResponse<SliceResponse<SiteChangeHistoryResponse>>> getSiteChangeHistories(
            @PathVariable Long id,
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest) {
        Slice<SiteChangeHistoryResponse> slice = siteService.getSiteChangeHistories(id,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));
        return ResponseEntity.ok(SuccessResponse.of(new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }
}
