//package com.lineinc.erp.api.server.presentation.v1.materialmanagement.controller;
//
//import com.lineinc.erp.api.server.application.materialmanagement.MaterialManagementService;
//import com.lineinc.erp.api.server.common.constant.AppConstants;
//import com.lineinc.erp.api.server.common.request.PageRequest;
//import com.lineinc.erp.api.server.common.request.SortRequest;
//import com.lineinc.erp.api.server.common.response.PagingInfo;
//import com.lineinc.erp.api.server.common.response.PagingResponse;
//import com.lineinc.erp.api.server.common.response.SuccessResponse;
//import com.lineinc.erp.api.server.common.util.DownloadFieldUtils;
//import com.lineinc.erp.api.server.common.util.PageableUtils;
//import com.lineinc.erp.api.server.common.util.ResponseHeaderUtils;
//import com.lineinc.erp.api.server.config.security.aop.RequireMenuPermission;
//import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
//import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostDownloadRequest;
//import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostListRequest;
//import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request.*;
//import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.response.MaterialManagementDetailViewResponse;
//import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.response.MaterialManagementResponse;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.springframework.data.domain.Page;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/material-managements")
//@RequiredArgsConstructor
//@Tag(name = "Material Management", description = "자재관리 관련 API")
//public class MaterialManagementController {
//
//    private final MaterialManagementService materialManagementService;
//
//    @Operation(
//            summary = "자재관리 등록",
//            description = "자재관리 정보를 등록합니다"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "자재관리 등록 성공"),
//            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 현장 또는 공정을 등록하려는 경우", content = @Content())
//    })
//    @PostMapping
//    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.CREATE)
//    public ResponseEntity<Void> createMaterialManagement(
//            @Valid @RequestBody MaterialManagementCreateRequest request
//    ) {
//        materialManagementService.createMaterialManagement(request);
//        return ResponseEntity.ok().build();
//    }
//
//    @Operation(
//            summary = "자재관리 목록 조회",
//            description = "필터 조건에 맞는 자재관리 목록을 조회합니다"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "자재관리 목록 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
//    })
//    @GetMapping
//    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.VIEW)
//    public ResponseEntity<SuccessResponse<PagingResponse<MaterialManagementResponse>>> getMaterialManagements(
//            @Valid PageRequest pageRequest,
//            @Valid SortRequest sortRequest,
//            @Valid MaterialManagementListRequest request
//    ) {
//
//        Page<MaterialManagementResponse> page = materialManagementService.getAllMaterialManagements(
//                request,
//                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort())
//        );
//
//        return ResponseEntity.ok(SuccessResponse.of(
//                new PagingResponse<>(PagingInfo.from(page), page.getContent())
//        ));
//    }
//
//    @Operation(
//            summary = "자재관리 삭제",
//            description = "하나 이상의 자재관리 ID를 받아 해당 데이터를 삭제합니다."
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "삭제 성공"),
//            @ApiResponse(responseCode = "400", description = "입력값 오류"),
//            @ApiResponse(responseCode = "404", description = "자재관리를 찾을 수 없음")
//    })
//    @DeleteMapping
//    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.DELETE)
//    public ResponseEntity<Void> deleteMaterialManagements(
//            @RequestBody DeleteMaterialManagementsRequest materialManagementIds
//    ) {
//        materialManagementService.deleteMaterialManagements(materialManagementIds);
//        return ResponseEntity.ok().build();
//    }
//
//    @Operation(
//            summary = "자재관리 목록 엑셀 다운로드",
//            description = "검색 조건에 맞는 자재관리 목록을 엑셀 파일로 다운로드합니다."
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
//            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
//    })
//    @GetMapping("/download")
//    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.VIEW)
//    public void downloadSitesExcel(
//            @Valid SortRequest sortRequest,
//            @Valid MaterialManagementListRequest request,
//            @Valid MaterialManagementDownloadRequest materialManagementDownloadRequest,
//            HttpServletResponse response
//    ) throws IOException {
//        List<String> parsed = DownloadFieldUtils.parseFields(materialManagementDownloadRequest.fields());
//        DownloadFieldUtils.validateFields(parsed, MaterialManagementDownloadRequest.ALLOWED_FIELDS);
//        ResponseHeaderUtils.setExcelDownloadHeader(response, "자재관리 목록.xlsx");
//
//        try (Workbook workbook = materialManagementService.downloadExcel(
//                request,
//                PageableUtils.parseSort(sortRequest.sort()),
//                parsed
//        )) {
//            workbook.write(response.getOutputStream());
//        }
//    }
//
//    @Operation(
//            summary = "자재관리 상세 조회",
//            description = "자재관리 상세 정보를 조회합니다"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "조회 성공"),
//            @ApiResponse(responseCode = "404", description = "자재관리를 찾을 수 없음", content = @Content())
//    })
//    @GetMapping("/{id}")
//    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.VIEW)
//    public ResponseEntity<SuccessResponse<MaterialManagementDetailViewResponse>> getMaterialManagementDetail(
//            @PathVariable Long id
//    ) {
//        MaterialManagementDetailViewResponse response = materialManagementService.getMaterialManagementById(id);
//        return ResponseEntity.ok(
//                SuccessResponse.of(response)
//        );
//    }
//
//    @Operation(
//            summary = "자재관리 정보 수정",
//            description = "자재관리 정보를 수정합니다"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "수정 성공"),
//            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
//            @ApiResponse(responseCode = "404", description = "해당 자재관리를 찾을 수 없음", content = @Content())
//    })
//    @PatchMapping("/{id}")
//    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.UPDATE)
//    public ResponseEntity<Void> updateMaterialManagement(
//            @PathVariable Long id,
//            @Valid @RequestBody MaterialManagementUpdateRequest request
//    ) {
//        materialManagementService.updateMaterialManagement(id, request);
//        return ResponseEntity.ok().build();
//    }
//}
//
//
