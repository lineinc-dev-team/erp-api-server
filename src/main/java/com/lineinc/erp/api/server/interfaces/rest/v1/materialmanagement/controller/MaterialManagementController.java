package com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementInputType;
import com.lineinc.erp.api.server.domain.materialmanagement.service.MaterialManagementChangeHistoryService;
import com.lineinc.erp.api.server.domain.materialmanagement.service.MaterialManagementService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.DeleteMaterialManagementsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementDownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementDetailViewResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementInputTypeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementNameResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.PageRequest;
import com.lineinc.erp.api.server.shared.dto.SortRequest;
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
@RequestMapping("/api/v1/material-managements")
@RequiredArgsConstructor
@Tag(name = "자재 관리", description = "자재관리 관련 API")
public class MaterialManagementController {

    private final MaterialManagementService materialManagementService;
    private final MaterialManagementChangeHistoryService changeHistoryService;

    @Operation(summary = "자재관리 등록", description = "자재관리 정보를 등록합니다")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "자재관리 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 현장 또는 공정을 등록하려는경우", content = @Content())
    })

    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createMaterialManagement(
            @Valid @RequestBody MaterialManagementCreateRequest request) {
        materialManagementService.createMaterialManagement(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "자재관리 투입구분 목록 조회", description = "자재관리 투입구분 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.VIEW)
    @GetMapping("/input-types")
    public ResponseEntity<SuccessResponse<List<MaterialManagementInputTypeResponse>>> getMaterialManagementInputTypes() {
        List<MaterialManagementInputTypeResponse> responseList = Arrays
                .stream(MaterialManagementInputType.values())
                .map(type -> new MaterialManagementInputTypeResponse(type.name(), type.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "자재관리 상세 품명 키워드 검색", description = "상세 품명으로 간단한 검색을 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/detail-names/search")
    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<MaterialManagementNameResponse>>> getMaterialManagementDetailNames(
            @Valid PageRequest pageRequest,
            @RequestParam(required = false) String keyword) {
        Pageable pageable = PageableUtils.createPageable(pageRequest.page(), pageRequest.size());
        Slice<MaterialManagementNameResponse> slice = materialManagementService.getMaterialManagementNames(keyword,
                pageable);

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "자재관리 목록 조회", description = "필터 조건에 맞는 자재관리 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "자재관리 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<MaterialManagementResponse>>> getMaterialManagements(
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid MaterialManagementListRequest request) {

        Page<MaterialManagementResponse> page = materialManagementService.getAllMaterialManagements(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "자재관리 삭제", description = "하나 이상의 자재관리 ID를 받아 해당 데이터를삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "자재관리를 찾을 수 없음")
    })
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteMaterialManagements(
            @RequestBody DeleteMaterialManagementsRequest materialManagementIds) {
        materialManagementService.deleteMaterialManagements(materialManagementIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "자재관리 목록 엑셀 다운로드", description = "검색 조건에 맞는 자재관리 목록을 엑셀 파일로 다운로드합니다.")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.VIEW)
    public void downloadSitesExcel(
            @Valid SortRequest sortRequest,
            @Valid MaterialManagementListRequest request,
            @Valid MaterialManagementDownloadRequest materialManagementDownloadRequest,
            HttpServletResponse response) throws IOException {
        List<String> parsed = DownloadFieldUtils.parseFields(materialManagementDownloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed,
                MaterialManagementDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "자재관리 목록.xlsx");

        try (Workbook workbook = materialManagementService.downloadExcel(
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed)) {
            workbook.write(response.getOutputStream());
        }
    }

    @Operation(summary = "자재관리 상세 조회", description = "자재관리 상세 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "자재관리를 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<MaterialManagementDetailViewResponse>> getMaterialManagementDetail(
            @PathVariable Long id) {
        MaterialManagementDetailViewResponse response = materialManagementService.getMaterialManagementById(id);
        return ResponseEntity.ok(
                SuccessResponse.of(response));
    }

    @Operation(summary = "자재관리 정보 수정", description = "자재관리 정보를 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "해당 자재관리를 찾을 수 없음", content = @Content())
    })
    @PatchMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateMaterialManagement(
            @PathVariable Long id,
            @Valid @RequestBody MaterialManagementUpdateRequest request) {
        materialManagementService.updateMaterialManagement(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "자재관리 수정이력 조회", description = "자재관리의 수정이력을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "자재관리를 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<MaterialManagementChangeHistoryResponse>>> getMaterialManagementChangeHistories(
            @PathVariable Long id,
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest) {

        Pageable pageable = PageableUtils.createPageable(pageRequest.page(),
                pageRequest.size(), sortRequest.sort());
        var slice = changeHistoryService.getChangeHistories(id, pageable);

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent().stream()
                        .map(MaterialManagementChangeHistoryResponse::from)
                        .toList())));
    }
}
