package com.lineinc.erp.api.server.presentation.v1.site.controller;

import com.lineinc.erp.api.server.application.site.SiteService;

import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.common.request.PageRequest;
import com.lineinc.erp.api.server.common.request.SortRequest;
import com.lineinc.erp.api.server.common.response.PagingInfo;
import com.lineinc.erp.api.server.common.response.PagingResponse;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.common.util.DownloadFieldUtils;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.common.util.ResponseHeaderUtils;
import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.DeleteSitesRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteDownloadRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteListRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.SiteDetailResponse;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.SiteResponse;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
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
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저 또는 발주처를 등록하려는 경우")
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createSite(@Valid @RequestBody SiteCreateRequest request) {
        siteService.createSite(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "현장 목록 조회", description = "등록된 모든 현장 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현장 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<PagingResponse<SiteResponse>>> getAllSites(
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid SiteListRequest request
    ) {
        Page<SiteResponse> page = siteService.getAllSites(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort())
        );

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())
        ));
    }

    @Operation(
            summary = "현장 목록 엑셀 다운로드",
            description = "검색 조건에 맞는 현장 목록을 엑셀 파일로 다운로드합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.VIEW)
    public void downloadSitesExcel(
            @Valid SortRequest sortRequest,
            @Valid SiteListRequest request,
            @Valid SiteDownloadRequest siteDownloadRequest,
            HttpServletResponse response
    ) throws IOException {
        List<String> parsed = DownloadFieldUtils.parseFields(siteDownloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed, SiteDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "현장 목록.xlsx");

        try (Workbook workbook = siteService.downloadExcel(
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed
        )) {
            workbook.write(response.getOutputStream());
        }
    }

    @Operation(summary = "현장 삭제", description = "하나 이상의 현장 ID를 받아 해당 현장을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현장 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "현장을 찾을 수 없음")
    })
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_SITE, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteSites(@RequestBody DeleteSitesRequest siteIds) {
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
            @PathVariable Long id
    ) {
        SiteDetailResponse siteResponse = siteService.getSiteById(id);
        return ResponseEntity.ok(SuccessResponse.of(siteResponse));
    }
}


