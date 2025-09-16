package com.lineinc.erp.api.server.interfaces.rest.v2.laborpayroll.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.laborpayroll.service.LaborPayrollService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollChangeHistoryResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.PageableUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "노무명세서 관리 V2", description = "노무명세서 조회 관련 V2 API")
@RestController
@RequestMapping("/api/v2/labor-payrolls")
@RequiredArgsConstructor
public class LaborPayrollV2Controller {

    private final LaborPayrollService laborPayrollService;

    /**
     * 노무명세서 변경이력 조회
     */
    @Operation(summary = "노무명세서 변경이력 조회", description = "특정 노무명세서 집계와 관련된 변경이력을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping("/summary/{id}/change-histories")
    @RequireMenuPermission(menu = AppConstants.MENU_LABOR_PAYROLL, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<LaborPayrollChangeHistoryResponse>>> getLaborPayrollChangeHistories(
            @Parameter(description = "노무명세서 집계 ID") @PathVariable Long id,
            @Parameter(description = "페이징 정보") @ModelAttribute PageRequest pageRequest,
            @Parameter(description = "정렬 정보") @ModelAttribute SortRequest sortRequest) {

        Page<LaborPayrollChangeHistoryResponse> page = laborPayrollService.getLaborPayrollChangeHistoriesWithPaging(
                id,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())));
    }

}
