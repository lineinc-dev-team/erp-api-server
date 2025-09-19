package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType;
import com.lineinc.erp.api.server.domain.managementcost.service.v1.ManagementCostService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.DeleteManagementCostsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostDownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ItemDescriptionResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ItemTypeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostDetailViewResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostResponse;
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
            @Valid @RequestBody final ManagementCostCreateRequest request) {
        managementCostService.createManagementCost(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관리비 항목 구분 조회", description = "사용 가능한 관리비 항목 구분 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/item-types")
    @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<List<ItemTypeResponse>>> getItemTypes() {
        final List<ItemTypeResponse> itemTypes = Arrays.stream(ManagementCostItemType.values())
                .map(ItemTypeResponse::from)
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(itemTypes));
    }

    @Operation(summary = "관리비 ETC 항목 설명 키워드 검색", description = "itemType이 ETC인 모든 관리비의 항목 설명을 키워드로 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/etc-item-type-descriptions/search")
    @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<ItemDescriptionResponse>>> searchEtcItemDescriptions(
            @RequestParam(required = false) final String keyword,
            @ModelAttribute final PageRequest pageRequest) {
        final Slice<ItemDescriptionResponse> slice = managementCostService.getEtcItemDescriptions(
                keyword, PageableUtils.createPageable(pageRequest.page(), pageRequest.size()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "관리비 삭제", description = "하나 이상의 관리비 ID를 받아 해당 데이터를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "관리비를 찾을 수 없음")
    })
    @DeleteMapping
    @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteManagementCosts(
            @RequestBody final DeleteManagementCostsRequest managementCostIds) {
        managementCostService.deleteManagementCosts(managementCostIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관리비 목록 조회", description = "필터 조건에 맞는 관리비 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리비 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping
    @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<ManagementCostResponse>>> getManagementCosts(
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest,
            @Valid final ManagementCostListRequest request) {

        final Page<ManagementCostResponse> page = managementCostService.getAllManagementCosts(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

    @Operation(summary = "관리비 상세 조회", description = "관리비 상세 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "관리비를 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<ManagementCostDetailViewResponse>> getManagementCostDetail(
            @PathVariable final Long id) {
        final ManagementCostDetailViewResponse response = managementCostService.getManagementCostById(id);

        return ResponseEntity.ok(
                SuccessResponse.of(response));
    }

    @Operation(summary = "관리비 목록 엑셀 다운로드", description = "검색 조건에 맞는 관리비 목록을 엑셀 파일로 다운로드합니다.")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content())
    })
    @GetMapping("/download")
    @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action = PermissionAction.VIEW)
    public void downloadSitesExcel(
            @Valid final SortRequest sortRequest,
            @Valid final ManagementCostListRequest request,
            @Valid final ManagementCostDownloadRequest managementCostDownloadRequest,
            final HttpServletResponse response) throws IOException {
        final List<String> parsed = DownloadFieldUtils.parseFields(managementCostDownloadRequest.fields());
        DownloadFieldUtils.validateFields(parsed,
                ManagementCostDownloadRequest.ALLOWED_FIELDS);
        ResponseHeaderUtils.setExcelDownloadHeader(response, "관리비 목록.xlsx");

        try (Workbook workbook = managementCostService.downloadExcel(
                request,
                PageableUtils.parseSort(sortRequest.sort()),
                parsed)) {
            workbook.write(response.getOutputStream());
        }
    }

    @Operation(summary = "관리비 정보 수정", description = "관리비 정보를 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "해당 관리비를 찾을 수 없음", content = @Content())
    })
    @PatchMapping("/{id}")
    @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateManagementCost(
            @PathVariable final Long id,
            @Valid @RequestBody final ManagementCostUpdateRequest request) {
        managementCostService.updateManagementCost(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관리비 수정이력 조회", description = "관리비의 수정이력을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "관리비를 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_MANAGEMENT_COST, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<ManagementCostChangeHistoryResponse>>> getManagementCostChangeHistories(
            @PathVariable final Long id,
            @Valid final PageRequest pageRequest,
            @Valid final SortRequest sortRequest) {
        final Slice<ManagementCostChangeHistoryResponse> slice = managementCostService.getManagementCostChangeHistories(
                id,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));
        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }
}
