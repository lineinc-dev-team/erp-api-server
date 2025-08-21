package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.controller;

import com.lineinc.erp.api.server.domain.managementcost.service.ManagementCostService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostCreateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/management-costs")
@RequiredArgsConstructor
@Tag(name = "관리비 관리", description = "관리비 관련 API")
public class ManagementCostController {

    private final ManagementCostService managementCostService;

    @Operation(summary = "관리비 등록", description = "관리비 정보를 등록합니다")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "관리비등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 현장 또는 공정을 등록하려는경우")
    })

    @PostMapping
    @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createManagementCost(
            @Valid @RequestBody ManagementCostCreateRequest request) {
        managementCostService.createManagementCost(request);
        return ResponseEntity.ok().build();
    }

    // @Operation(summary = "관리비 삭제", description = "하나 이상의 관리비 ID를 받아 해당 데이터를
    // 삭제합니다.")
    // @ApiResponses(value = {
    // @ApiResponse(responseCode = "200", description = "삭제 성공"),
    // @ApiResponse(responseCode = "400", description = "입력값 오류"),
    // @ApiResponse(responseCode = "404", description = "관리비를 찾을 수 없음")
    // })
    // @DeleteMapping
    // @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action =
    // PermissionAction.DELETE)
    // public ResponseEntity<Void> deleteManagementCosts(
    // @RequestBody DeleteManagementCostsRequest managementCostIds) {
    // managementCostService.deleteManagementCosts(managementCostIds);
    // return ResponseEntity.ok().build();
    // }

    // @Operation(summary = "관리비 목록 조회", description = "필터 조건에 맞는 관리비 목록을 조회합니다")
    // @ApiResponses(value = {
    // @ApiResponse(responseCode = "200", description = "관리비 목록 조회 성공"),
    // @ApiResponse(responseCode = "400", description = "입력값 오류", content =
    // @Content())
    // })
    // @GetMapping
    // @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action =
    // PermissionAction.VIEW)
    // public
    // ResponseEntity<SuccessResponse<PagingResponse<ManagementCostResponse>>>
    // getManagementCosts(
    // @Valid PageRequest pageRequest,
    // @Valid SortRequest sortRequest,
    // @Valid ManagementCostListRequest request) {

    // Page<ManagementCostResponse> page =
    // managementCostService.getAllManagementCosts(
    // request,
    // PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
    // sortRequest.sort()));

    // return ResponseEntity.ok(SuccessResponse.of(
    // new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    // }

    // @Operation(summary = "관리비 상세 조회", description = "관리비 상세 정보를 조회합니다")
    // @ApiResponses(value = {
    // @ApiResponse(responseCode = "200", description = "조회 성공"),
    // @ApiResponse(responseCode = "404", description = "관리비를 찾을 수 없음", content =
    // @Content())
    // })
    // @GetMapping("/{id}")
    // @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action =
    // PermissionAction.VIEW)
    // public ResponseEntity<SuccessResponse<ManagementCostDetailViewResponse>>
    // getManagementCostDetail(
    // @PathVariable Long id) {
    // ManagementCostDetailViewResponse response =
    // managementCostService.getManagementCostById(id);

    // return ResponseEntity.ok(
    // SuccessResponse.of(response));
    // }

    // @Operation(summary = "관리비 목록 엑셀 다운로드", description = "검색 조건에 맞는 관리비 목록을 엑셀
    // 파일로 다운로드합니다.")
    // @ApiResponses(value = {
    // @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
    // @ApiResponse(responseCode = "400", description = "입력값 오류", content =
    // @Content())
    // })
    // @GetMapping("/download")
    // @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action =
    // PermissionAction.VIEW)
    // public void downloadSitesExcel(
    // @Valid SortRequest sortRequest,
    // @Valid ManagementCostListRequest request,
    // @Valid ManagementCostDownloadRequest managementCostDownloadRequest,
    // HttpServletResponse response) throws IOException {
    // List<String> parsed =
    // DownloadFieldUtils.parseFields(managementCostDownloadRequest.fields());
    // DownloadFieldUtils.validateFields(parsed,
    // ManagementCostDownloadRequest.ALLOWED_FIELDS);
    // ResponseHeaderUtils.setExcelDownloadHeader(response, "관리비 목록.xlsx");

    // try (Workbook workbook = managementCostService.downloadExcel(
    // request,
    // PageableUtils.parseSort(sortRequest.sort()),
    // parsed)) {
    // workbook.write(response.getOutputStream());
    // }
    // }

    // @Operation(summary = "관리비 정보 수정", description = "관리비 정보를 수정합니다")
    // @ApiResponses(value = {
    // @ApiResponse(responseCode = "200", description = "수정 성공"),
    // @ApiResponse(responseCode = "400", description = "입력값 오류", content =
    // @Content()),
    // @ApiResponse(responseCode = "404", description = "해당 관리비를 찾을 수 없음", content =
    // @Content())
    // })
    // @PatchMapping("/{id}")
    // @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action =
    // PermissionAction.UPDATE)
    // public ResponseEntity<Void> updateManagementCost(
    // @PathVariable Long id,
    // @Valid @RequestBody ManagementCostUpdateRequest request) {
    // managementCostService.updateManagementCost(id, request);
    // return ResponseEntity.ok().build();
    // }
}
