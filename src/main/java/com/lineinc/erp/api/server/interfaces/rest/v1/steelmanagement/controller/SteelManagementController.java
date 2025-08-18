package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.controller;

import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType;
import com.lineinc.erp.api.server.domain.steelmanagement.service.SteelManagementService;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.ApproveSteelManagementRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.DeleteSteelManagementRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.ReleaseSteelManagementRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementDownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementDetailViewResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementTypeResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.PageRequest;
import com.lineinc.erp.api.server.shared.dto.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
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
@RequestMapping("/api/v1/steel-managements")
@RequiredArgsConstructor
@Tag(name = "강재수불부 관리", description = "강재수불부 관리 API")
public class SteelManagementController {
    private final SteelManagementService steelManagementService;

    @Operation(summary = "강재수불부 등록", description = "강재수불부 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.CREATE)
    public ResponseEntity<SuccessResponse<Long>> createSteelManagement(
            @Valid @RequestBody SteelManagementCreateRequest request) {
        steelManagementService.createSteelManagement(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강재 관리 수정", description = "강재 관리 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "강재 관리를 찾을 수 없음")
    })
    @PatchMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateSteelManagement(
            @PathVariable Long id,
            @Valid @RequestBody SteelManagementUpdateRequest request) {
        steelManagementService.updateSteelManagement(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강재수불부 목록 조회", description = "등록된 강재수불부 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류") })
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<SteelManagementResponse>>> getSteelManagementList(
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid SteelManagementListRequest request) {
        Page<SteelManagementResponse> page = steelManagementService.getSteelManagementList(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "강재수불부 구분 목록 조회", description = "강재수불부 구분 목록을 반환합니다")
    @GetMapping("/steel-management-types")
    public ResponseEntity<SuccessResponse<List<SteelManagementTypeResponse>>> getSteelManagementTypes() {
        List<SteelManagementTypeResponse> responseList = Arrays.stream(SteelManagementType.values())
                .map(type -> new SteelManagementTypeResponse(type.name(), type.getLabel()))
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

    @Operation(summary = "강재수불부 삭제", description = "하나 이상의 강재수불부 ID를 받아 해당 데이터를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "강재수불부를 찾을 수 없음")
    })
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteSteelManagements(
            @Valid @RequestBody DeleteSteelManagementRequest request) {
        steelManagementService.deleteSteelManagements(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강재수불부 승인 처리", description = "하나 이상의 강재수불부 ID를 받아 구분값을 승인으로 변경합니다.")

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인 처리 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "강재수불부를 찾을 수 없음")
    })
    @PatchMapping("/approve")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.APPROVE)
    public ResponseEntity<Void> approveSteelManagement(
            @Valid @RequestBody ApproveSteelManagementRequest request) {
        steelManagementService.approveSteelManagements(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강재수불부 반출 처리", description = "하나 이상의 강재수불부 ID를 받아 구분값을 반출로 변경합니다.")

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "반출 처리 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "강재수불부를 찾을 수 없음")
    })
    @PatchMapping("/release")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<Void> releaseSteelManagements(
            @Valid @RequestBody ReleaseSteelManagementRequest request) {
        steelManagementService.releaseSteelManagements(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강재수불부 엑셀 다운로드", description = "검색 조건에 맞는 강재수불부 목록을 엑셀 파일로 다운로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류")
    })
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.VIEW)
    public void downloadSteelManagementsExcel(
            @Valid SortRequest sortRequest,
            @Valid SteelManagementListRequest request,
            @Valid SteelManagementDownloadRequest steelManagementDownloadRequest,
            HttpServletResponse response) throws java.io.IOException {
        List<String> parsed = DownloadFieldUtils.parseFields(steelManagementDownloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed,
                SteelManagementDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "강재수불부 목록.xlsx");

        try (Workbook workbook = steelManagementService.downloadExcel(
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed)) {
            workbook.write(response.getOutputStream());
        }
    }

    @Operation(summary = "강재수불부 상세 조회", description = "강재수불부 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "강재수불부를 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SteelManagementDetailViewResponse>> getSteelManagementDetail(
            @PathVariable Long id) {
        SteelManagementDetailViewResponse steelManagementDetailViewResponse = steelManagementService
                .getSteelManagementById(id);
        return ResponseEntity.ok(SuccessResponse.of(steelManagementDetailViewResponse));
    }
}
