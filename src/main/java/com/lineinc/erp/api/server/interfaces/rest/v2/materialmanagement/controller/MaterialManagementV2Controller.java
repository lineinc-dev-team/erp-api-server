package com.lineinc.erp.api.server.interfaces.rest.v2.materialmanagement.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.materialmanagement.service.MaterialManagementChangeHistoryService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementChangeHistoryResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.PageableUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/material-managements")
@RequiredArgsConstructor
@Tag(name = "자재 관리 V2", description = "자재관리 관련 V2 API")
public class MaterialManagementV2Controller {

    private final MaterialManagementChangeHistoryService materialManagementChangeHistoryService;

    @Operation(summary = "자재관리 수정이력 조회", description = "자재관리의 수정이력을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "자재관리를 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_MATERIAL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<MaterialManagementChangeHistoryResponse>>> getMaterialManagementChangeHistories(
            @PathVariable Long id,
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest) {

        Pageable pageable = PageableUtils.createPageable(pageRequest.page(),
                pageRequest.size(), sortRequest.sort());
        var page = materialManagementChangeHistoryService.getChangeHistoriesWithPaging(id, pageable);

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }
}
