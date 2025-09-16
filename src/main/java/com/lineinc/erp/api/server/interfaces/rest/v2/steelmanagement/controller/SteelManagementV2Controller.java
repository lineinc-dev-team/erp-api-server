package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.steelmanagement.service.SteelManagementChangeHistoryService;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementChangeHistoryResponse;
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
@RequestMapping("/api/v2/steel-managements")
@RequiredArgsConstructor
@Tag(name = "강재수불부 관리 V2", description = "강재수불부 관리 V2 API")
public class SteelManagementV2Controller {
    private final SteelManagementChangeHistoryService steelManagementChangeHistoryService;

    @Operation(summary = "강재수불부 수정이력 조회", description = "강재수불부의 수정이력을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "강재수불부를 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<SteelManagementChangeHistoryResponse>>> getSteelManagementChangeHistories(
            @PathVariable Long id,
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest) {

        Pageable pageable = PageableUtils.createPageable(pageRequest.page(),
                pageRequest.size(), sortRequest.sort());
        var page = steelManagementChangeHistoryService.getSteelManagementChangeHistoryWithPaging(id, pageable);

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }
}
